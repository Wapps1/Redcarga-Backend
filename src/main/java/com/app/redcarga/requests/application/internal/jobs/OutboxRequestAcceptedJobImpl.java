package com.app.redcarga.requests.application.internal.jobs;

import com.app.redcarga.requests.application.internal.gateways.RequestsOutboxGateway;
import com.app.redcarga.requests.domain.services.RequestCommandService;
import com.app.redcarga.shared.events.requests.RequestAcceptedEvent;
import com.app.redcarga.shared.events.requests.RequestAcceptedClearedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxRequestAcceptedJobImpl implements OutboxRequestAcceptedJob {

    private final RequestsOutboxGateway outboxGateway;
    private final RequestCommandService requestCommandService;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void runBatch(int maxBatchSize) {
        var pending = outboxGateway.fetchPendingRequestAccepted(maxBatchSize);
        int processed = 0;
        for (var e : pending) {
            if (processed >= maxBatchSize) break;
            if ("request.accepted.v1".equals(e.eventType())) {
                try {
                    RequestAcceptedEvent evt = mapper.readValue(e.payload(), RequestAcceptedEvent.class);
                    Integer rid = evt.requestId() != null ? evt.requestId() : e.requestId();
                    if (rid != null) {
                        requestCommandService.markAsAccepted(rid, evt.quoteId());
                    } else {
                        log.warn("[OutboxReqAccepted] missing requestId for outbox {}", e.id());
                    }
                    outboxGateway.markProcessed(e.id());
                    processed++;
                } catch (Exception ex) {
                    log.warn("[OutboxReqAccepted] failed outboxId={} err={}", e.id(), ex.toString());
                    // leave unprocessed to retry
                }
                continue;
            }

            if ("request.accepted.cleared.v1".equals(e.eventType())) {
                try {
                    RequestAcceptedClearedEvent evt = mapper.readValue(e.payload(), RequestAcceptedClearedEvent.class);
                    Integer rid = evt.requestId() != null ? evt.requestId() : e.requestId();
                    if (rid != null) {
                        requestCommandService.clearAcceptedQuoteIfMatches(rid, evt.quoteId());
                    } else {
                        log.warn("[OutboxReqAcceptedCleared] missing requestId for outbox {}", e.id());
                    }
                    outboxGateway.markProcessed(e.id());
                    processed++;
                } catch (Exception ex) {
                    log.warn("[OutboxReqAcceptedCleared] failed outboxId={} err={}", e.id(), ex.toString());
                    // leave unprocessed to retry
                }
                continue;
            }

            // skip unknown event types for requests route
            outboxGateway.markProcessed(e.id());
            continue;
            
        }
        if (processed > 0) log.info("[OutboxReqAccepted] processed={} entries", processed);
    }
}
