package com.app.redcarga.providers.interfaces.rest.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyAndRegisterCompanyRequest(
        @NotNull Integer accountId,

        @NotBlank @Size(min = 1, max = 200)
        String legalName,

        @Size(min = 1, max = 200)
        String tradeName,

        @NotBlank
        String ruc,

        @NotBlank @Email
        String email,

        @NotBlank
        String phone,

        @Size(max = 400)
        String address
) {}
