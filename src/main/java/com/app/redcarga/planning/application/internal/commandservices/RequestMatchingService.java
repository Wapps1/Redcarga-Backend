package com.app.redcarga.planning.application.internal.commandservices;

import com.app.redcarga.planning.application.internal.events.NewRequestMatchesReady;
import com.app.redcarga.planning.application.internal.gateways.ProviderRouteMatchingGateway;
import com.app.redcarga.planning.application.internal.gateways.RequestInboxStore;
import com.app.redcarga.planning.application.internal.outboundservices.events.PlanningEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestMatchingService {

    private final ProviderRouteMatchingGateway matching;
    private final RequestInboxStore inboxStore;
    private final PlanningEventPublisher events;
    private final Clock clock;

    /**
     * Orquesta el matching, persiste en inbox (idempotente)
     * y emite un evento con todos los datos para notificar por WS AFTER_COMMIT.
     */
    @Transactional
    public void matchAndNotify(int requestId,
                               String originDepartmentCode,
                               String originProvinceCode,
                               String destDepartmentCode,
                               String destProvinceCode,
                               Instant createdAt,
                               boolean dryRun,
                               String requesterName,

                               // ===== NUEVO: datos para preview =====
                               String originDepartmentName,
                               String originProvinceName,
                               String destDepartmentName,
                               String destProvinceName,
                               Integer totalQuantity) {

        Instant ts = (createdAt != null) ? createdAt : Instant.now(clock);

        var candidates = matching.findBestPerCompany(
                originProvinceCode, originDepartmentCode, destProvinceCode, destDepartmentCode
        );

        if (!dryRun) {
            for (var c : candidates) {
                inboxStore.insertIfAbsent(requestId,
                        c.companyId(),
                        c.routeId(),
                        c.routeTypeId(),
                        ts,
                        requesterName,
                        originDepartmentName,
                        originProvinceName,
                        destDepartmentName,
                        destProvinceName,
                        totalQuantity);
            }
        }

        if (candidates.isEmpty()) {
            return; // nada que notificar
        }

        List<NewRequestMatchesReady.CompanyMatch> matches = candidates.stream()
                .map(c -> new NewRequestMatchesReady.CompanyMatch(c.companyId(), c.routeId(), c.routeTypeId()))
                .toList();

        // Evento extendido con nombres y totalQuantity
        var evt = new NewRequestMatchesReady(
                requestId,
                originDepartmentCode,
                originProvinceCode,
                destDepartmentCode,
                destProvinceCode,
                ts,
                requesterName,
                originDepartmentName,
                originProvinceName,
                destDepartmentName,
                destProvinceName,
                totalQuantity,
                matches
        );

        events.publish(evt); // AFTER_COMMIT
    }
}
