package com.app.redcarga.requests.infrastructure.persistence.jdbc;

import com.app.redcarga.requests.domain.repositories.RequestAcceptanceRepository;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PgRequestRepository implements RequestAcceptanceRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    @Transactional
    public void markAsAccepted(Integer requestId, Integer acceptedQuoteId) {
        if (requestId == null || acceptedQuoteId == null) throw new IllegalArgumentException("request_or_quote_required");
        // Only set accepted_quote_id if it's not already set (idempotent). If already set to the same value, do nothing.
        int updated = jdbc.update(
                "UPDATE requests.requests SET accepted_quote_id = :acceptedQuoteId WHERE request_id = :requestId AND accepted_quote_id IS NULL",
                Map.of("acceptedQuoteId", acceptedQuoteId, "requestId", requestId)
        );

        if (updated == 1) return; // success

        // If no rows updated, either request not found or already has an accepted_quote_id. Check current state.
        var rows = jdbc.queryForList(
                "SELECT accepted_quote_id FROM requests.requests WHERE request_id = :requestId",
                Map.of("requestId", requestId)
        );

        if (rows.isEmpty()) throw new DomainException("request_not_found");

        Object current = rows.get(0).get("accepted_quote_id");
        if (current == null) {
            // Shouldn't happen, but defensive: try once more and fail if still not updated
            int retry = jdbc.update(
                    "UPDATE requests.requests SET accepted_quote_id = :acceptedQuoteId WHERE request_id = :requestId AND accepted_quote_id IS NULL",
                    Map.of("acceptedQuoteId", acceptedQuoteId, "requestId", requestId)
            );
            if (retry == 1) return;
            throw new DomainException("failed_to_mark_accepted");
        }

        Integer existing = (current instanceof Integer) ? (Integer) current : (current == null ? null : Integer.valueOf(current.toString()));
        if (existing.equals(acceptedQuoteId)) {
            // idempotent: already accepted with same quote
            return;
        }

        // Already accepted with a different quote
        throw new DomainException("request_already_accepted");
    }

    @Override
    @Transactional
    public void clearAcceptedQuoteIfMatches(Integer requestId, Integer quoteId) {
    if (requestId == null || quoteId == null) throw new IllegalArgumentException("request_or_quote_required");
    int updated = jdbc.update(
        "UPDATE requests.requests SET accepted_quote_id = NULL WHERE request_id = :requestId AND accepted_quote_id = :quoteId",
        Map.of("requestId", requestId, "quoteId", quoteId)
    );
    if (updated == 1) return; // cleared successfully

    // If nothing updated, check whether the request exists; if not, raise not found. If exists and accepted_quote_id is different or null, do nothing.
    var rows = jdbc.queryForList(
        "SELECT accepted_quote_id FROM requests.requests WHERE request_id = :requestId",
        Map.of("requestId", requestId)
    );
    if (rows.isEmpty()) throw new DomainException("request_not_found");
    // otherwise: accepted_quote_id is either null or different than quoteId => no action needed
    }
}