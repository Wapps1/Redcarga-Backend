package com.app.redcarga.providers.domain.repositories;

import com.app.redcarga.providers.domain.model.aggregates.CompanyMember;

public interface CompanyMemberRepository {
    CompanyMember save(CompanyMember member);
    boolean existsActiveByAccountId(Integer accountId); // opcional para reglas futuras
    boolean existsActiveByCompanyIdAndAccountId(Integer companyId, Integer accountId);
}

