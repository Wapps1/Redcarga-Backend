package com.app.redcarga.deals.infrastructure.outbound;

import com.app.redcarga.deals.infrastructure.persistence.jpa.entities.DealsOutboxEntry;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaDealsOutboxRepository;
import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealsChatOutboxAdapter {

    private final JpaDealsOutboxRepository outbox;
    private final ObjectMapper mapper;

    /** Persist user chat message into outbox (route_kind=quote-chat, event=USER_MESSAGE). */
    public void persistUserMessageOutbox(ChatMessageDto dto) {
        try {
            DealsOutboxEntry e = new DealsOutboxEntry();
            e.setRouteKind("quote-chat");
            e.setQuoteId(dto.quoteId());
            e.setEventType("USER_MESSAGE");
            e.setPayload(mapper.writeValueAsString(dto));
            outbox.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("outbox_serialization_error", ex);
        }
    }
}
