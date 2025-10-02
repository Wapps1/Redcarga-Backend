package com.app.redcarga.providers.application.internal.outboundservices.persistence;

public interface CompanyComplianceGateway {
    void ensurePending(Integer companyId);
}
