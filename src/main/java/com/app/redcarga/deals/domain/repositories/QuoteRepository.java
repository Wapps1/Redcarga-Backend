package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.aggregates.Quote;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
    Quote save(Quote quote);
    List<Quote> findByRequestIdAndStateCode(Integer requestId, String stateCode);
    List<Quote> findByRequestId(Integer requestId);
}
