package com.app.redcarga.providers.application.internal.acl;

import com.app.redcarga.providers.application.internal.views.CompanyNamesView;
import com.app.redcarga.providers.domain.model.valueobjects.CompanyStatus;
import com.app.redcarga.providers.domain.repositories.CompanyRepository;
import com.app.redcarga.providers.domain.services.CompanyQueryService;
import com.app.redcarga.providers.interfaces.acl.CompaniesCatalogFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class CompaniesCatalogFacadeImpl implements CompaniesCatalogFacade {

    private final CompanyRepository companyRepository;
    private final CompanyQueryService companyQueryService;

    @Override
    public boolean existsCompany(int companyId) {
        return companyRepository.findById(companyId).isPresent();
    }

    @Override
    public boolean isActive(int companyId) {
        return companyRepository.findById(companyId)
                .map(c -> c.getStatus() == CompanyStatus.VERIFIED)
                .orElse(false);
    }
    @Override
    public Optional<CompanyNamesView> getCompanyNames(int companyId) {
        return companyQueryService.getCompanyNames(companyId);
    }
}


