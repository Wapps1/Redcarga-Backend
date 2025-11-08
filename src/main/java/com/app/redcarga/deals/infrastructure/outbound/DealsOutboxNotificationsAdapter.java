package com.app.redcarga.deals.infrastructure.outbound;

import com.app.redcarga.deals.application.internal.outboundservices.notifications.NewQuoteNotification;
import com.app.redcarga.deals.application.internal.outboundservices.notifications.NotificationsPort;
import com.app.redcarga.deals.infrastructure.persistence.jpa.entities.DealsOutboxEntry;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaDealsOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DealsOutboxNotificationsAdapter implements NotificationsPort {

    private final JpaDealsOutboxRepository outbox;
    private final ObjectMapper mapper;

    @Override
    public void publishNewQuote(NewQuoteNotification notification) {
        try {
            DealsOutboxEntry e = new DealsOutboxEntry();
            e.setRouteKind("request-pending");
            e.setRequestId(notification.requestId());
            e.setQuoteId(notification.quoteId());
            e.setEventType("QUOTE_CREATED");
            e.setPayload(mapper.writeValueAsString(notification));
            outbox.save(e);
        } catch (Exception ex) {
            throw new RuntimeException("outbox_serialization_error", ex);
        }
    }
}
