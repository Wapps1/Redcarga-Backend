package com.app.redcarga.providers.domain.model.commands;

public record VerifyAndRegisterCompanyCommand(
        Integer accountId,
        String legalName,
        String tradeName,
        String ruc,
        String email,
        String phone,
        String address
) {}
