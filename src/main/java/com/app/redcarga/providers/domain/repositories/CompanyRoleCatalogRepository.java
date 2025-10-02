package com.app.redcarga.providers.domain.repositories;

import com.app.redcarga.providers.domain.model.entities.CompanyRole;

import java.util.Optional;

public interface CompanyRoleCatalogRepository {
    Optional<CompanyRole> findByCode(String code); // "ADMIN"
}
