package com.app.redcarga.providers.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.providers.domain.model.entities.CompanyRole;
import com.app.redcarga.providers.domain.repositories.CompanyRoleCatalogRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCompanyRoleCatalogRepository
        extends JpaRepository<CompanyRole, Integer>, CompanyRoleCatalogRepository {

    @Override
    Optional<CompanyRole> findByCode(String code);
}
