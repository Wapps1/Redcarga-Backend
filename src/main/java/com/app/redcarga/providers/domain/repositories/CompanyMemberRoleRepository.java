package com.app.redcarga.providers.domain.repositories;

import com.app.redcarga.providers.domain.model.entities.CompanyMemberRole;

import java.util.List;

public interface CompanyMemberRoleRepository {
    CompanyMemberRole save(CompanyMemberRole role);

    List<String> findActiveRoleCodes(int companyId, int accountId);
    boolean hasAnyActiveRole(int companyId, int accountId, java.util.List<String> roleCodes);
}
