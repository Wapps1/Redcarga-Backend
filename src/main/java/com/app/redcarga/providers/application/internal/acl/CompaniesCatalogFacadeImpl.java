package com.app.redcarga.providers.application.internal.acl;

import com.app.redcarga.providers.domain.model.valueobjects.CompanyStatus;
import com.app.redcarga.providers.domain.repositories.CompanyRepository;
import com.app.redcarga.providers.interfaces.acl.CompaniesCatalogFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CompaniesCatalogFacadeImpl implements CompaniesCatalogFacade {

    private final CompanyRepository companyRepository;

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
}


