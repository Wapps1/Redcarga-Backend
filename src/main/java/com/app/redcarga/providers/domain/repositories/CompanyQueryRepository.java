package com.app.redcarga.providers.domain.repositories;

import com.app.redcarga.providers.application.internal.views.CompanyView;
import com.app.redcarga.providers.application.internal.views.CompanyNamesView;
import java.util.Optional;

public interface CompanyQueryRepository {
    Optional<CompanyView> findById(int companyId);
    boolean hasActiveMembership(int companyId, int accountId);

    // Returns only the company's names (legalName, tradeName)
    Optional<CompanyNamesView> findNamesById(int companyId);
}