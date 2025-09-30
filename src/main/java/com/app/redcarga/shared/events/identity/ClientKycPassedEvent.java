package com.app.redcarga.shared.events.identity;

import java.time.Instant;

public record ClientKycPassedEvent(
        String eventId,
        Instant occurredAt,
        int accountId,
        int schemaVersion
) {}
