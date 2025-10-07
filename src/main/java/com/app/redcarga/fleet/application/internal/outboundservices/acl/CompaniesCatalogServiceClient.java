package com.app.redcarga.fleet.application.internal.outboundservices.acl;

import com.app.redcarga.providers.interfaces.acl.CompaniesCatalogFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompaniesCatalogServiceClient implements CompaniesCatalogService {

    private final CompaniesCatalogFacade companies;

    @Override
    public boolean existsCompany(int companyId) {
        return companies.existsCompany(companyId);
    }

    @Override
    public boolean isActive(int companyId) {
        return companies.isActive(companyId);
    }
}


