package com.app.redcarga.fleet.interfaces.rest.responses;

public record VehicleView(
        Integer vehicleId,
        Integer companyId,
        String name,
        String plate,
        boolean active,
        Long createdAt,
        Long updatedAt
) {}


