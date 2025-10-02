package com.app.redcarga.providers.domain.services;


import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterCompanyCommand;

public interface CompanyCommandService {
    Integer handle(VerifyAndRegisterCompanyCommand cmd); // devuelve companyId
}

