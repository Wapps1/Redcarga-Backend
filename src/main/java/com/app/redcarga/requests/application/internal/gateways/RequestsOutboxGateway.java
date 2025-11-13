package com.app.redcarga.requests.application.internal.gateways;

import java.util.List;

/**
 * Gateway to read integration outbox entries targeted to the requests BC.
 * Implementation should live in requests.infrastructure and access the shared outbox table.
 */
public interface RequestsOutboxGateway {

    record OutboxRow(Integer id, String eventType, String payload, Integer requestId, Integer quoteId) {}

    /** Fetch up to 'limit' pending rows for route_kind = 'requests', ordered by created_at asc. */
    List<OutboxRow> fetchPendingRequestAccepted(int limit);

    /** Mark an outbox row processed (set processed_at = now()). */
    void markProcessed(Integer outboxId);
}
