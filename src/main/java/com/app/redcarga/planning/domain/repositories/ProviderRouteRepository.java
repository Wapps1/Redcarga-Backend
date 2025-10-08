package com.app.redcarga.planning.domain.repositories;

import com.app.redcarga.planning.domain.model.aggregates.ProviderRoute;

import java.util.Optional;

public interface ProviderRouteRepository {

    ProviderRoute save(ProviderRoute route);

    Optional<ProviderRoute> findById(Integer routeId);

    // Para prevenir duplicados antes de golpear la unicidad de BD
    boolean existsDD(Integer companyId, String originDepartmentCode, String destDepartmentCode);

    boolean existsPP(Integer companyId, String originProvinceCode, String destProvinceCode);

    void delete(ProviderRoute route);
}
