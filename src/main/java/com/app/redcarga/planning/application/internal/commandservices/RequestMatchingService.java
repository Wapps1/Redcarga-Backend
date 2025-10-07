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
    private final Clock clock; // inyecta un Clock bean para testear tiempos

    /**
     * Orquesta el matching, opcionalmente persiste en inbox (idempotente)
     * y emite un evento para notificar por WS AFTER_COMMIT.
     */
    @Transactional
    public void matchAndNotify(int requestId,
                               String originProvinceCode,
                               String originDepartmentCode,
                               String destProvinceCode,
                               String destDepartmentCode,
                               Instant createdAt,
                               boolean dryRun,
                               String requesterName) {

        Instant ts = (createdAt != null) ? createdAt : Instant.now(clock);

        var candidates = matching.findBestPerCompany(
                originProvinceCode, originDepartmentCode, destProvinceCode, destDepartmentCode
        );

        if (!dryRun) {
            for (var c : candidates) {
                inboxStore.insertIfAbsent(requestId, c.companyId(), c.routeId(), c.routeTypeId(), ts);
            }
        }

        if (candidates.isEmpty()) {
            // Nada que notificar; salimos silenciosamente
            return;
        }

        // Construye el payload de evento (solo datos mínimos)
        List<NewRequestMatchesReady.CompanyMatch> matches = candidates.stream()
                .map(c -> new NewRequestMatchesReady.CompanyMatch(c.companyId(), c.routeId(), c.routeTypeId()))
                .toList();

        var evt = new NewRequestMatchesReady(
                requestId,
                originDepartmentCode,
                originProvinceCode,
                destDepartmentCode,
                destProvinceCode,
                ts,
                requesterName,
                matches
        );

        // Publica AFTER_COMMIT vía el publisher de infraestructura
        events.publish(evt);
    }
}
