package com.app.redcarga.deals.application.internal.outboundservices.acl;

import com.app.redcarga.providers.interfaces.acl.ProvidersMembershipFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Outbound ACL client: deals BC consumes providers BC membership facade.
 */
@Service
@RequiredArgsConstructor
public class ProvidersMembershipClient {

    private final ProvidersMembershipFacade providersMembershipFacade;

    public boolean isMemberOfCompany(int companyId, int accountId) {
        return providersMembershipFacade.isMemberOfCompany(companyId, accountId);
    }

    public boolean hasAnyRole(int companyId, int accountId, int... roleCodes) {
        return providersMembershipFacade.hasAnyRole(companyId, accountId, roleCodes);
    }
}
