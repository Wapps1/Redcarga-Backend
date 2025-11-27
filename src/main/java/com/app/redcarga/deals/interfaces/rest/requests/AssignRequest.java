package com.app.redcarga.deals.interfaces.rest.requests;

import jakarta.validation.constraints.NotNull;

public record AssignRequest(
        @NotNull Integer driverId,
        @NotNull Integer vehicleId
) {}