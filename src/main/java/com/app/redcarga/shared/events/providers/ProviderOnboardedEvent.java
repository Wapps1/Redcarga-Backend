package com.app.redcarga.shared.events.providers;

public record ProviderOnboardedEvent(
        java.util.UUID eventId,
        java.time.Instant occurredAt,
        Integer accountId,
        Integer schemaVersion
) {}
