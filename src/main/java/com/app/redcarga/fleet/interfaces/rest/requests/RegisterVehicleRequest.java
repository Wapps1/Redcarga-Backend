package com.app.redcarga.fleet.interfaces.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterVehicleRequest(
        @NotBlank String name,
        @NotBlank String plate,
        @NotNull Boolean active
) {}


