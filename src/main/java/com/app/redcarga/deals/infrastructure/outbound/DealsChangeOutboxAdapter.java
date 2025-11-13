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

    /** Persist an outbox entry for a change to quote chat (message + info.change). */
    public void persistChangeOutbox(Change change) {
        try {
            DealsOutboxEntry e = new DealsOutboxEntry();
            e.setRouteKind("quote-chat");
            e.setQuoteId(change.getQuote().getId());
            e.setEventType("SYSTEM_MESSAGE");
            var dto = buildPayload(change);
            e.setPayload(mapper.writeValueAsString(dto));
            outbox.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("outbox_serialization_error", ex);
        }
    }

    private Object buildPayload(Change change) {
        var info = new java.util.HashMap<String, Object>();
        info.put("changeId", change.getChangeId());
        info.put("quoteId", change.getQuote().getId());
        info.put("kind", change.getKindCode());
        info.put("status", change.getStatusCode());
        info.put("createdBy", change.getCreatedBy());
        info.put("createdAt", change.getCreatedAt());
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
        info.put("items", items);

        var message = new java.util.HashMap<String, Object>();
        message.put("messageId", null);
        message.put("quoteId", change.getQuote().getId());
        message.put("typeCode", "SYSTEM");
        message.put("contentCode", "CHANGE");
        message.put("body", "Cambio aplicado");
        message.put("createdBy", change.getCreatedBy());
        message.put("createdAt", change.getCreatedAt());
        message.put("system_subtype_code", "CHANGE_APPLIED");
        message.put("info", info);
        return message;
    }
}
