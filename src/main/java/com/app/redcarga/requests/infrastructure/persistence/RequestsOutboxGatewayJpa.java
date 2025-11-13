package com.app.redcarga.requests.infrastructure.persistence;

import com.app.redcarga.requests.application.internal.gateways.RequestsOutboxGateway;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaDealsOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestsOutboxGatewayJpa implements RequestsOutboxGateway {

    private final JpaDealsOutboxRepository repo;

    @Override
    public List<OutboxRow> fetchPendingRequestAccepted(int limit) {
        var rows = repo.findByRouteKindAndProcessedAtIsNullOrderByCreatedAtAsc("requests");
        return rows.stream().limit(limit).map(e -> new OutboxRow(
                e.getId(), e.getEventType(), e.getPayload(), e.getRequestId(), e.getQuoteId()
        )).collect(Collectors.toList());
    }

    @Override
    public void markProcessed(Integer outboxId) {
        repo.findById(outboxId).ifPresent(e -> {
            e.setProcessedAt(Instant.now());
            repo.save(e);
        });
    }

    // No attempts/last_error in current schema; failures will be retried on next poll until processed_at is set
}
