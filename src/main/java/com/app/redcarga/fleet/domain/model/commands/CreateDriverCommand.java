package com.app.redcarga.fleet.domain.model.commands;

public record CreateDriverCommand(
        Integer companyId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String licenseNumber,
        Boolean active
) {}


