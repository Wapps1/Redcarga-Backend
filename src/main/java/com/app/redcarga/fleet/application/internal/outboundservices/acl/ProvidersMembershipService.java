package com.app.redcarga.fleet.application.internal.outboundservices.acl;

public interface ProvidersMembershipService {

    boolean hasAnyRole(int companyId, int accountId, int... roleCodes);
}
