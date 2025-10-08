package com.app.redcarga.providers.domain.queries;

import com.app.redcarga.providers.application.internal.views.CompanyView;
import java.util.Optional;

public interface CompanyQueryRepository {
    Optional<CompanyView> findById(int companyId);
    boolean hasActiveMembership(int companyId, int accountId);
}