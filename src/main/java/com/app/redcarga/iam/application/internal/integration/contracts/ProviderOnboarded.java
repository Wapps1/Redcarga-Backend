package com.app.redcarga.iam.application.internal.integration.contracts;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Onboarding de proveedor completado → DONE")
public record ProviderOnboarded(
        @NotNull UUID eventId,
        @NotNull Instant occurredAt,
        @NotNull Integer accountId,
        @NotNull Integer schemaVersion
) {}
