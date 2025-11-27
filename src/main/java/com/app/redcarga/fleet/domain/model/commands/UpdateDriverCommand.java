package com.app.redcarga.fleet.domain.model.commands;

public record UpdateDriverCommand(
        Integer driverId,
        String licenseNumber,
        Boolean active
) {}


