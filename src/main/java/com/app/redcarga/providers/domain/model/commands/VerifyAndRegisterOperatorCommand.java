package com.app.redcarga.providers.domain.model.commands;

public record VerifyAndRegisterOperatorCommand (
    Integer adminId,
    Integer operatorId,
    Integer companyId,
    String roleId
){}
