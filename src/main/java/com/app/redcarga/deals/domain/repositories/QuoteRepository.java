package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.aggregates.Quote;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
    Quote save(Quote quote);
    Optional<Quote> findById(Integer id);
    List<Quote> findByRequestIdAndStateCode(Integer requestId, String stateCode);
    List<Quote> findByRequestId(Integer requestId);
    /** Bulk update helper: set toState for quotes in requestId except excludedQuoteId when they are currently fromState. Returns rows updated. */
    int updateStateForRequestExcept(Integer requestId, Integer excludedQuoteId, String fromState, String toState);
}
