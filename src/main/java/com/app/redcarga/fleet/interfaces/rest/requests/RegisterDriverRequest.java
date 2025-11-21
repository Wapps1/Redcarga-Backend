package com.app.redcarga.fleet.interfaces.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDriverRequest(
        Integer accountId,
        String licenseNumber,
        @NotNull Boolean active
) {}


