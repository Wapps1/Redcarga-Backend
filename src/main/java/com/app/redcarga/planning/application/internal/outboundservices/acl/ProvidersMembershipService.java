package com.app.redcarga.planning.application.internal.outboundservices.acl;

import java.util.List;

/** Cliente interno tipado para membership/roles en Providers. */
public interface ProvidersMembershipService {
    boolean isMemberOfCompany(int companyId, int accountId);
    boolean hasAnyRole(int companyId, int accountId, List<String> roleCodes);
}
