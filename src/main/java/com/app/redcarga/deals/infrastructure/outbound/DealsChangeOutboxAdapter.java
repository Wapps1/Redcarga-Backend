package com.app.redcarga.deals.infrastructure.outbound;

import com.app.redcarga.deals.infrastructure.persistence.jpa.entities.DealsOutboxEntry;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaDealsOutboxRepository;
import com.app.redcarga.deals.domain.model.entities.Change;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealsChangeOutboxAdapter {

    private final JpaDealsOutboxRepository outbox;
    private final ObjectMapper mapper;

    /**
     * Persist an outbox entry for a change. This does NOT publish; it only saves a row to the deals.outbox table
     * with route_kind='quote-chat' and eventType='CHANGE_APPLIED' (caller decides eventType).
     */
    public void persistChangeOutbox(Change change) {
        try {
            DealsOutboxEntry e = new DealsOutboxEntry();
            e.setRouteKind("quote-chat");
            e.setQuoteId(change.getQuote().getId());
            e.setEventType("CHANGE_APPLIED");
            // Build minimal payload: change + items
            var dto = buildPayload(change);
            e.setPayload(mapper.writeValueAsString(dto));
            outbox.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("outbox_serialization_error", ex);
        }
    }

    private Object buildPayload(Change change) {
        var payload = new java.util.HashMap<String, Object>();
        payload.put("changeId", change.getChangeId());
        payload.put("quoteId", change.getQuote().getId());
        payload.put("kind", change.getKindCode());
        payload.put("status", change.getStatusCode());
        payload.put("createdBy", change.getCreatedBy());
        payload.put("createdAt", change.getCreatedAt());
        var items = new java.util.ArrayList<java.util.Map<String,Object>>();
        for (var it : change.getItems()) {
            var m = new java.util.HashMap<String,Object>();
            m.put("changeItemId", it.getChangeItemId());
            m.put("fieldCode", it.getFieldCode());
            m.put("targetQuoteItemId", it.getTargetQuoteItemId());
            m.put("targetRequestItemId", it.getTargetRequestItemId());
            m.put("oldValue", it.getOldValue());
            m.put("newValue", it.getNewValue());
            items.add(m);
        }
        payload.put("items", items);
        return payload;
    }
}
