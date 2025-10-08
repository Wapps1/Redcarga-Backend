package com.app.redcarga.fleet.domain.model.commands;

public record UpdateDriverCommand(
        Integer driverId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String licenseNumber,
        Boolean active
) {}


