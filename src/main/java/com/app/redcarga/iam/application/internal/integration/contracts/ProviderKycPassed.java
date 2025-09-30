package com.app.redcarga.iam.application.internal.integration.contracts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "KYC aprobado para cuentas PROVIDER → BASIC_PROFILE_COMPLETED")
public record ProviderKycPassed(
        @NotNull UUID eventId,
        @NotNull Instant occurredAt,
        @NotNull Integer accountId,
        @NotNull Integer schemaVersion
) {}
