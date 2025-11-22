package com.app.redcarga.providers.application.internal.acl;

import com.app.redcarga.providers.domain.model.queries.IsActiveMemberQuery;
import com.app.redcarga.providers.domain.services.CompanyMembershipQueryService;
import com.app.redcarga.providers.domain.repositories.CompanyMemberRepository;
import com.app.redcarga.providers.interfaces.acl.ProvidersMembershipFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.List;

@Component
@RequiredArgsConstructor
class ProvidersMembershipFacadeImpl implements ProvidersMembershipFacade {

    private final CompanyMembershipQueryService queryService;
    private final CompanyMemberRepository memberRepo;

    @Override
    public boolean isMemberOfCompany(int companyId, int accountId) {
        return queryService.isActiveMember(new IsActiveMemberQuery(companyId, accountId));
    }

    @Override
    public boolean hasAnyRole(int companyId, int accountId, int... roleCodes) {
        List<Integer> codes = (roleCodes == null)
                ? List.of()
                : Arrays.stream(roleCodes).boxed().toList();

        return queryService.hasAnyRole(companyId, accountId, codes);
    }

    @Override
    public Optional<Integer> findAnyActiveCompanyIdByAccount(int accountId) {
        return memberRepo.findAnyActiveByAccount(accountId);
    }

    @Override
    public List<String> getActiveRoleCodes(int companyId, int accountId) {
        return queryService.getActiveRoleCodes(companyId, accountId);
    }
}
