package com.app.redcarga.requests.application.internal.commandservices;

import com.app.redcarga.requests.application.internal.config.RequestsOutboxProperties;
import com.app.redcarga.requests.application.internal.gateways.OutboxPlanningMatchGateway;
import com.app.redcarga.requests.application.internal.outboundservices.acl.GeoCatalogService;
import com.app.redcarga.requests.application.internal.outboundservices.acl.IdentityPersonService;
import com.app.redcarga.requests.application.internal.outboundservices.acl.PlanningInboxCloseClient;
import com.app.redcarga.requests.application.internal.outboundservices.acl.PlanningMatchingClient;
import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.model.commands.CloseRequestCommand;
import com.app.redcarga.requests.domain.model.commands.CreateRequestCommand;
import com.app.redcarga.requests.domain.model.commands.CreateRequestItemCommand;
import com.app.redcarga.requests.domain.model.commands.CreateRequestItemImageCommand;
import com.app.redcarga.requests.domain.model.entities.RequestItem;
import com.app.redcarga.requests.domain.model.entities.RequestItemImage;
import com.app.redcarga.requests.domain.model.valueobjects.ProvinceCode;
import com.app.redcarga.requests.domain.model.valueobjects.RequestStatusCode;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import com.app.redcarga.requests.domain.services.RequestCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestCommandServiceImpl implements RequestCommandService {

    private final RequestRepository requests;
    private final GeoCatalogService geo;
    private final IdentityPersonService identity;
    private final OutboxPlanningMatchGateway outboxMatch;
    private final PlanningInboxCloseClient planningClose;
    private final Clock clock;
    private final RequestsOutboxProperties props;
    private final PlanningMatchingClient planning;

    /* ============================ CREATE ============================ */
    @Override
    @Transactional
    public Integer create(CreateRequestCommand cmd) {
        // 1) Validación UBIGEO por ACL (sin joins inter-BC)
        var oDep = cmd.origin().getDepartmentCode().getValue();
        var dDep = cmd.destination().getDepartmentCode().getValue();

        if (!geo.existsDepartmentCode(oDep)) throw new DomainException("origin_department_not_found");
        if (!geo.existsDepartmentCode(dDep)) throw new DomainException("dest_department_not_found");

        var oProv = Optional.ofNullable(cmd.origin().getProvinceCode()).map(ProvinceCode::getValue).orElse(null);
        var dProv = Optional.ofNullable(cmd.destination().getProvinceCode()).map(ProvinceCode::getValue).orElse(null);

        if (oProv != null && !geo.existsProvinceCode(oProv)) throw new DomainException("origin_province_not_found");
        if (dProv != null && !geo.existsProvinceCode(dProv)) throw new DomainException("dest_province_not_found");

        // 2) Resolver snapshot de nombre si no vino
        String requesterNameSnapshot = normalizeOrFetchName(cmd.requesterNameSnapshot(), cmd.requesterAccountId());

        // 3) Construir ítems (con imágenes)
        List<RequestItem> items = new ArrayList<>();
        if (cmd.items() != null && !cmd.items().isEmpty()) {
            for (CreateRequestItemCommand it : cmd.items()) {
                RequestItem item = RequestItem.of(
                        it.itemName(),
                        it.heightCm(),
                        it.widthCm(),
                        it.lengthCm(),
                        it.weightKg(),
                        it.totalWeightKg(),
                        it.quantity(),
                        it.fragile(),
                        it.notes()
                );

                // imágenes (si tu comando ya las trae)
                List<CreateRequestItemImageCommand> imgs = it.images();
                if (imgs != null && !imgs.isEmpty()) {
                    // respeta imagePosition si viene; si no, el aggregate autonumera
                    imgs.stream()
                            .sorted(Comparator.comparing(i -> Optional.ofNullable(i.imagePosition()).orElse(Integer.MAX_VALUE)))
                            .forEach(i -> item.addImage(RequestItemImage.of(i.imageUrl())));
                }
                items.add(item);
            }
        }

        // 4) Crear aggregate en estado OPEN con ≥1 ítem (la factory valida eso)
        Request request = Request.createOpen(
                cmd.requesterAccountId(),
                requesterNameSnapshot,
                cmd.origin(),
                cmd.destination(),
                items,
                cmd.paymentOnDelivery()
        );

        // 5) Persistir
        Request saved = requests.save(request);
        Integer requestId = saved.getId();

        // 6) Encolar outbox con createdAt REAL de la entidad (auditing)
        Instant createdAt = saved.getCreatedAt().toInstant();
        outboxMatch.enqueueMatch(requestId, oDep, oProv, dDep, dProv, createdAt, createdAt);

        if (props.isAlsoFireAfterCommit()) {
            afterCommit(() -> {
                var name = normalizeOrFetchName(null, cmd.requesterAccountId()); // o usa saved.getRequesterNameSnapshot()
                planning.matchAndNotify(
                        requestId, oDep, oProv, dDep, dProv,
                        saved.getCreatedAt().toInstant(),
                        name == null ? "" : name
                );
            });
        }

        return requestId;
    }

    /* ============================ CLOSE ============================ */
    @Override
    @Transactional
    public void close(CloseRequestCommand cmd) {
        var req = requests.findById(cmd.requestId())
                .orElseThrow(() -> new DomainException("request_not_found"));

        var from = req.currentStatus();
        if (from == RequestStatusCode.CLOSED || from == RequestStatusCode.CANCELLED || from == RequestStatusCode.EXPIRED)
            throw new DomainException("request_already_closed");

        // Transición final según razón
        switch (cmd.reason()) {
            case USER_CLOSED, MATCHED -> req.close();  // CLOSED
            case CANCELLED            -> req.cancel(); // CANCELLED
            case TIMEOUT              -> req.expire(); // EXPIRED
        }

        requests.save(req);

        // TODO: escribir en requests.request_status_history (siguiente iteración)

        // AFTER_COMMIT: cerrar inbox en Planning (notifica a proveedores)
        afterCommit(() -> planningClose.closeInboxForRequest(cmd.requestId(), true));
    }

    /* ============================ Helpers ============================ */

    private String normalizeOrFetchName(String snapshot, Integer accountId) {
        String v = snapshot == null ? null : snapshot.trim();
        if (v != null && !v.isEmpty()) return v;
        return identity.getByAccountId(accountId)
                .map(IdentityPersonService.PersonSnapshot::fullName)
                .orElse(null); // no bloqueamos si no hay nombre
    }

    private static void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { action.run(); }
            });
        } else {
            action.run();
        }
    }
}
