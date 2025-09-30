package com.app.redcarga.iam.application.internal.integration.contracts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "KYC aprobado para cuentas CLIENT → DONE")
public record ClientKycPassed(
        @NotNull @Schema(example = "3d9b3e2b-5e6a-4f2a-9fd3-4fb49b6c2d77") UUID eventId,
        @NotNull @Schema(example = "2025-09-28T23:59:59Z") Instant occurredAt,
        @NotNull @Schema(example = "123") Integer accountId,
        @NotNull @Schema(example = "1") Integer schemaVersion
) {}