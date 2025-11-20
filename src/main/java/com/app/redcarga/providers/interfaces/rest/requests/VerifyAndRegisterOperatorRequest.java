package com.app.redcarga.providers.interfaces.rest.requests;

import jakarta.validation.constraints.NotNull;

public record VerifyAndRegisterOperatorRequest(
        @NotNull Integer operatorId,
        @NotNull String roleId
) {}
