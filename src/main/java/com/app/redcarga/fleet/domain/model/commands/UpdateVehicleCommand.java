package com.app.redcarga.fleet.domain.model.commands;

public record UpdateVehicleCommand(
        Integer vehicleId,
        String name,
        String plate,
        Boolean active
) {}


