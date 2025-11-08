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
                // only handle request-pending QUOTE_CREATED for now
                if (!"request-pending".equals(e.getRouteKind()) || !"QUOTE_CREATED".equals(e.getEventType())) {
                    e.setProcessedAt(Instant.now());
                    outboxRepository.save(e);
                    continue;
                }
                // parse payload to extract requestId
                var node = objectMapper.readTree(e.getPayload());
                Integer requestId = node.has("requestId") && !node.get("requestId").isNull() ? node.get("requestId").asInt() : null;
                if (requestId == null) {
                    e.setProcessedAt(Instant.now());
                    outboxRepository.save(e);
                    continue;
                }
                var optReq = requestQueryService.findById(requestId);
                if (optReq.isEmpty()) {
                    e.setProcessedAt(Instant.now());
                    outboxRepository.save(e);
                    continue;
                }
                int requesterAccountId = optReq.get().getRequesterAccountId();
                String dest = String.format(Destinations.TOPIC_REQUEST_ACCOUNT_QUOTES, requesterAccountId);
                // forward original payload as-is
                messagingTemplate.convertAndSend(dest, e.getPayload());
                e.setProcessedAt(Instant.now());
                outboxRepository.save(e);
            } catch (Exception ex) {
                // log and skip (do not mark processed to allow retry)
                System.err.println("[OutboxPublisher] failed to publish entry id=" + e.getId() + " ex=" + ex);
            }
        }
    }
}
