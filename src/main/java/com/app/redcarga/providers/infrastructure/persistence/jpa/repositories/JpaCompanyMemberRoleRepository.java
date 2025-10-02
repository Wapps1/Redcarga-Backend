package com.app.redcarga.providers.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.providers.domain.model.entities.CompanyMemberRole;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyMemberRoleId;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCompanyMemberRoleRepository
        extends JpaRepository<CompanyMemberRole, CompanyMemberRoleId>, CompanyMemberRoleRepository {
    // save(...) heredado cumple la interfaz del dominio
}
