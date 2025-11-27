package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.aggregates.Quote;

// IMPORTS CORRECTOS
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
    Quote save(Quote quote);
    Optional<Quote> findById(Integer id);
    List<Quote> findByRequestIdAndStateCode(Integer requestId, String stateCode);
    List<Quote> findByRequestId(Integer requestId);
    /** Bulk update helper: set toState for quotes in requestId except excludedQuoteId when they are currently fromState. Returns rows updated. */
    int updateStateForRequestExcept(Integer requestId, Integer excludedQuoteId, String fromState, String toState);
    // New finders for company filters
    List<Quote> findByCompanyId(Integer companyId);
    List<Quote> findByCompanyIdAndStateCode(Integer companyId, String stateCode);
    List<Quote> findByCompanyIdAndStateCodeIn(Integer companyId, Collection<String> stateCodes);

    List<Quote> findByRequestIdIn(List<Integer> requestIds);
    List<Quote> findByRequestIdInAndStateCode(List<Integer> requestIds, String stateCode);

    Optional<Integer> findVersionByQuoteId(Integer quoteId);
}
