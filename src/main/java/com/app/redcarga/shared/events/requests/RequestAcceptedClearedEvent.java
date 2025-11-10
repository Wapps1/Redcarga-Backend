package com.app.redcarga.shared.events.requests;

import java.time.Instant;

/** Event published by deals when a previously accepted quote is later rejected and the request should clear its accepted_quote_id. */
public record RequestAcceptedClearedEvent(
        String eventId,
        Instant occurredAt,
        Integer requestId,
        Integer quoteId,
        Integer actorAccountId,
        int schemaVersion
) {}
