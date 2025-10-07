package com.app.redcarga.fleet.domain.model.commands;

public record CreateVehicleCommand(
        Integer companyId,
        String name,
        String plate,
        Boolean active
) {}


