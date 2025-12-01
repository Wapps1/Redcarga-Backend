package com.app.redcarga.providers.domain.services;

import com.app.redcarga.providers.application.internal.views.CompanyView;
import com.app.redcarga.providers.application.internal.views.CompanyNamesView;
import java.util.Optional;

public interface CompanyQueryService {
    Optional<CompanyView> getCompany(int companyId);

    // Lightweight company names view (legalName, tradeName)
    Optional<CompanyNamesView> getCompanyNames(int companyId);
}
