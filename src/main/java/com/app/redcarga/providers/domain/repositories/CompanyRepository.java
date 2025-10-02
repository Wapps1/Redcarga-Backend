package com.app.redcarga.providers.domain.repositories;

import com.app.redcarga.providers.domain.model.aggregates.Company;

import java.util.Optional;

public interface CompanyRepository {
    Company save(Company company);
    Optional<Company> findById(Integer id);
    boolean existsByRuc(String ruc);
    boolean existsByCreatedByAccountId(Integer accountId);
}
