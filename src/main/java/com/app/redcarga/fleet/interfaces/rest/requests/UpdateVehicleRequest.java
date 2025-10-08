package com.app.redcarga.fleet.interfaces.rest.requests;

public record UpdateVehicleRequest(
        String name,
        String plate,
        Boolean active
) {}


