package com.app.redcarga.fleet.domain.model.commands;

public record CreateDriverCommand(
        Integer companyId,
        Integer accountId,
        String licenseNumber,
        Boolean active
) {}


