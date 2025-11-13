package com.app.redcarga.shared.events.requests;

import java.time.Instant;

/** Event published by deals when an acceptance is confirmed. */
public record RequestAcceptedEvent(
        String eventId,
        Instant occurredAt,
        Integer requestId,
        Integer quoteId,
        Integer acceptanceId,
        Integer resolverAccountId,
        int schemaVersion
) {}
