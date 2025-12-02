package com.app.redcarga.requests.domain.repositories;

import java.util.Optional;

public interface RequestAcceptanceRepository {
    void markAsAccepted(Integer requestId, Integer acceptedQuoteId);
    void clearAcceptedQuoteIfMatches(Integer requestId, Integer quoteId);
    Optional<Integer> findAcceptedQuoteId(Integer requestId);
}