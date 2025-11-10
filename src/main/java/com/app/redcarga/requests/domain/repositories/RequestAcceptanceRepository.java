package com.app.redcarga.requests.domain.repositories;

public interface RequestAcceptanceRepository {
    void markAsAccepted(Integer requestId, Integer acceptedQuoteId);
    void clearAcceptedQuoteIfMatches(Integer requestId, Integer quoteId);
}