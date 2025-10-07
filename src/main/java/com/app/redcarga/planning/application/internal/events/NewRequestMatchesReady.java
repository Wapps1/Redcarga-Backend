package com.app.redcarga.planning.application.internal.events;

import java.time.Instant;
import java.util.List;

/**
 * Evento interno de aplicación emitido cuando termina el matching.
 * Se publica AFTER_COMMIT a través de PlanningEventPublisher.
 */
public record NewRequestMatchesReady(
        int requestId,
        String originDepartmentCode,
        String originProvinceCode,  // puede ser null en DD
        String destDepartmentCode,
        String destProvinceCode,    // puede ser null en DD
        Instant createdAt,
        String requesterName,
        List<CompanyMatch> matches  // una por company
) {
    public record CompanyMatch(int companyId, int routeId, int routeTypeId) {}
}
