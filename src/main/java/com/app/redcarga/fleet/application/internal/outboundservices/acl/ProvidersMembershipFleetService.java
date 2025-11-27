package com.app.redcarga.fleet.application.internal.outboundservices.acl;

public interface ProvidersMembershipFleetService {

    boolean hasAnyRole(int companyId, int accountId, int... roleCodes);

    boolean isMemberOfCompany(int companyId, int accountId);
}
