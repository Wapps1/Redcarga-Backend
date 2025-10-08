package com.app.redcarga.providers.application.internal.queryservices;

import com.app.redcarga.providers.application.internal.views.CompanyView;
import java.util.Optional;

public interface CompanyQueryService {
    Optional<CompanyView> getCompany(int companyId);
}
