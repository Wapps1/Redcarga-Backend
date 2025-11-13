package com.app.redcarga.deals.infrastructure.outbound;

import com.app.redcarga.deals.infrastructure.persistence.jpa.entities.DealsOutboxEntry;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaDealsOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Enqueue integration/application events into deals.outbox (processedAt = null means pending). */
@Component
@RequiredArgsConstructor
public class DealsOutboxPublisher {

    private final JpaDealsOutboxRepository outbox;

    /** Enqueue an event targeting another route/BC. */
    public void enqueue(String routeKind, String eventType, Integer requestId, Integer quoteId, String payloadJson) {
        try {
            DealsOutboxEntry e = new DealsOutboxEntry();
            e.setRouteKind(routeKind);
            e.setEventType(eventType);
            e.setRequestId(requestId);
            e.setQuoteId(quoteId);
            e.setPayload(payloadJson);
            outbox.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("outbox_enqueue_error", ex);
        }
    }
}
