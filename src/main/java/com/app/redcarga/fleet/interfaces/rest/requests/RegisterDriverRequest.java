package com.app.redcarga.fleet.interfaces.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDriverRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String email,
        String phone,
        String licenseNumber,
        @NotNull Boolean active
) {}


