package com.app.redcarga.providers.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.providers.domain.model.aggregates.Company;
import com.app.redcarga.providers.domain.repositories.CompanyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCompanyRepository
        extends JpaRepository<Company, Integer>, CompanyRepository {

    // Implementa la firma del dominio con un JPQL explícito
    @Override
    @Query("select (count(c) > 0) from Company c where c.ruc.value = :ruc")
    boolean existsByRuc(String ruc);

    // Esta firma la resuelve Spring Data por nombre del campo
    @Override
    boolean existsByCreatedByAccountId(Integer accountId);
}
