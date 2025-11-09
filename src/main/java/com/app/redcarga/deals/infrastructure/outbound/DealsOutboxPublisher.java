package com.app.redcarga.deals.infrastructure.outbound;

import com.app.redcarga.deals.infrastructure.persistence.jpa.entities.DealsOutboxEntry;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaDealsOutboxRepository;
import com.app.redcarga.shared.ws.Destinations;
import com.app.redcarga.requests.domain.services.RequestQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DealsOutboxPublisher {

    private final JpaDealsOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final RequestQueryService requestQueryService;

    @Scheduled(fixedDelayString = "${deals.outbox.poll-ms:2000}")
    @Transactional
    public void pollAndPublish() {
        List<DealsOutboxEntry> entries = outboxRepository.findByProcessedAtIsNullOrderByCreatedAtAsc();
        for (DealsOutboxEntry e : entries) {
            try {
                if ("request-pending".equals(e.getRouteKind()) && "QUOTE_CREATED".equals(e.getEventType())) {
                    // parse payload to extract requestId
                    var node = objectMapper.readTree(e.getPayload());
                    Integer requestId = node.has("requestId") && !node.get("requestId").isNull() ? node.get("requestId").asInt() : null;
                    if (requestId != null) {
                        var optReq = requestQueryService.findById(requestId);
                        if (optReq.isPresent()) {
                            int requesterAccountId = optReq.get().getRequesterAccountId();
                            String dest = String.format(Destinations.TOPIC_REQUEST_ACCOUNT_QUOTES, requesterAccountId);
                            messagingTemplate.convertAndSend(dest, node); // send parsed JSON
                        }
                    }
                    e.setProcessedAt(Instant.now());
                    outboxRepository.save(e);
                    continue;
                }

                if ("quote-chat".equals(e.getRouteKind())) {
                    Integer quoteId = e.getQuoteId();
                    if (quoteId != null) {
                        String dest = String.format(Destinations.TOPIC_DEALS_QUOTES_CHAT, quoteId);
                        var node = objectMapper.readTree(e.getPayload());
                        messagingTemplate.convertAndSend(dest, node);
                    }
                    e.setProcessedAt(Instant.now());
                    outboxRepository.save(e);
                    continue;
                }

                // Unknown route/event: mark processed to avoid retry storms
                e.setProcessedAt(Instant.now());
                outboxRepository.save(e);
            } catch (Exception ex) {
                // log and skip (do not mark processed to allow retry)
                System.err.println("[OutboxPublisher] failed to publish entry id=" + e.getId() + " ex=" + ex);
            }
        }
    }
}
