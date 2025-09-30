package com.app.redcarga.identity.interfaces.rest.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VerifyAndCreateRequest(
        @NotNull Integer accountId,
        @NotBlank String fullName,
        @NotBlank String docTypeCode,   // "DNI" | "CE" | "PAS"
        @NotBlank String docNumber,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate
) {}
