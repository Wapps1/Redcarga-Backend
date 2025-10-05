package com.app.redcarga.planning.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.planning.domain.model.aggregates.ProviderRoute;
import com.app.redcarga.planning.domain.repositories.ProviderRouteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProviderRouteRepository
        extends JpaRepository<ProviderRoute, Integer>, ProviderRouteRepository {

    @Override
    Optional<ProviderRoute> findById(Integer routeId);

    // --- DD: mismas deps, provincias NULL ---
    @Override
    @Query("""
           select (count(r) > 0)
           from ProviderRoute r
           where r.companyId = :companyId
             and r.originDepartment.value = :originDepartmentCode
             and r.destDepartment.value   = :destDepartmentCode
             and r.originProvince is null
             and r.destProvince   is null
           """)
    boolean existsDD(Integer companyId, String originDepartmentCode, String destDepartmentCode);

    // --- PP: mismas provs, provincias NOT NULL ---
    @Override
    @Query("""
           select (count(r) > 0)
           from ProviderRoute r
           where r.companyId = :companyId
             and r.originProvince.value = :originProvinceCode
             and r.destProvince.value   = :destProvinceCode
             and r.originProvince is not null
             and r.destProvince   is not null
           """)
    boolean existsPP(Integer companyId, String originProvinceCode, String destProvinceCode);
}
