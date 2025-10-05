// com.app.redcarga.providers.application.internal.queryservices.CompanyMembershipQueryServiceImpl
package com.app.redcarga.providers.application.internal.queryservices;

import com.app.redcarga.providers.domain.model.queries.IsActiveMemberQuery;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRepository;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRoleRepository;
import com.app.redcarga.providers.domain.services.CompanyMembershipQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyMembershipQueryServiceImpl implements CompanyMembershipQueryService {

    private final CompanyMemberRepository memberRepo;
    private final CompanyMemberRoleRepository roleRepo;

    @Override
    public boolean isActiveMember(IsActiveMemberQuery q) {
        return memberRepo.existsActiveByCompanyIdAndAccountId(q.companyId(), q.accountId());
    }

    @Override
    public boolean hasAnyRole(int companyId, int accountId, List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return false;
        return roleRepo.hasAnyActiveRole(companyId, accountId, roleCodes);
    }

    @Override
    public List<String> getActiveRoleCodes(int companyId, int accountId) {
        return roleRepo.findActiveRoleCodes(companyId, accountId);
    }
}
