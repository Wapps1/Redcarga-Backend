package com.app.redcarga.deals.interfaces.rest.requests;

import jakarta.validation.constraints.NotNull;

public record AssignEditRequest(
        @NotNull Integer driverId,
        @NotNull Integer vehicleId,
        @NotNull Integer version
) {}