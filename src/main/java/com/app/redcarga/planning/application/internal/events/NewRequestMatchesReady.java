// package: com.app.redcarga.planning.application.internal.events
package com.app.redcarga.planning.application.internal.events;

import java.time.Instant;
import java.util.List;

public record NewRequestMatchesReady(
        int requestId,

        // Códigos (ORDEN: Dep → Prov, Dep → Prov)
        String originDepartmentCode,
        String originProvinceCode,
        String destDepartmentCode,
        String destProvinceCode,

        Instant createdAt,
        String requesterName,

        // NUEVO: datos para preview (pueden ir null por ahora)
        String originDepartmentName,
        String originProvinceName,
        String destDepartmentName,
        String destProvinceName,
        Integer totalQuantity,

        List<CompanyMatch> matches
) {
    public record CompanyMatch(int companyId, int routeId, int routeTypeId) {}
}
