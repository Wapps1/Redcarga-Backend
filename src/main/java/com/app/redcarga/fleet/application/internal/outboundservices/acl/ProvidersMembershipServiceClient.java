package com.app.redcarga.fleet.application.internal.outboundservices.acl;

import com.app.redcarga.providers.interfaces.acl.ProvidersMembershipFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvidersMembershipServiceClient implements ProvidersMembershipService {
    private final ProvidersMembershipFacade membershipFacade;

    @Override
    public boolean hasAnyRole(int companyId, int accountId, int... roleCodes){
        return membershipFacade.hasAnyRole(companyId, accountId, roleCodes);
    }
}
