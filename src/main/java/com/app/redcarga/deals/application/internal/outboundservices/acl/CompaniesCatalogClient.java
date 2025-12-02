package com.app.redcarga.deals.application.internal.outboundservices.acl;

import com.app.redcarga.providers.application.internal.views.CompanyNamesView;
import com.app.redcarga.providers.interfaces.acl.CompaniesCatalogFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompaniesCatalogClient {

    private final CompaniesCatalogFacade companiesCatalogFacade;

    public Optional<CompanyNamesView> getCompanyNames(int companyId) {
        return companiesCatalogFacade.getCompanyNames(companyId);
    }
}