package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.aggregates.Quote;

import java.util.List;

public interface QuoteQueryService {
    List<Quote> listByRequestIdAndState(Integer requestId, String stateCode);
}
