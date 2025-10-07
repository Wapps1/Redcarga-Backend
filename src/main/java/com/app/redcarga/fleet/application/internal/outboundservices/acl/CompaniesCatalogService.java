package com.app.redcarga.fleet.application.internal.outboundservices.acl;

public interface CompaniesCatalogService {
    boolean existsCompany(int companyId);
    boolean isActive(int companyId);
}


