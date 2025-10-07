package com.app.redcarga.providers.interfaces.acl;

public interface CompaniesCatalogFacade {
    boolean existsCompany(int companyId);
    boolean isActive(int companyId);
}


