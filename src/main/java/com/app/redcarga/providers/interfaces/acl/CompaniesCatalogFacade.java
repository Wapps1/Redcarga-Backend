package com.app.redcarga.providers.interfaces.acl;

import com.app.redcarga.providers.application.internal.views.CompanyNamesView;
import java.util.Optional;

public interface CompaniesCatalogFacade {
    boolean existsCompany(int companyId);
    boolean isActive(int companyId);

    Optional<CompanyNamesView> getCompanyNames(int companyId);
}