package com.app.redcarga.planning.application.internal.gateways;

import java.util.List;

/**
 * Puerto de aplicación para resolver candidatos de matching (PP ∪ DD) y
 * deduplicar por company (prioridad PP sobre DD).
 * Implementación: infrastructure.persistence.jdbc.PgProviderRouteMatchingGateway
 */
public interface ProviderRouteMatchingGateway {

    /**
     * Retorna a lo más un candidato por company.
     */
    List<Candidate> findBestPerCompany(
            String originProvinceCode,
            String originDepartmentCode,
            String destProvinceCode,
            String destDepartmentCode
    );

    /**
     * routeTypeId corresponde al catálogo planning.route_types.
     */
    record Candidate(int routeId, int companyId, int routeTypeId) {}
}
