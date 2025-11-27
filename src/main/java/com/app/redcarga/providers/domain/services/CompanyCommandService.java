package com.app.redcarga.providers.domain.services;


import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterCompanyCommand;
import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterOperatorCommand;

public interface CompanyCommandService {
    Integer handle(VerifyAndRegisterCompanyCommand cmd); // devuelve companyId
    String registerOperator(VerifyAndRegisterOperatorCommand c);
}

