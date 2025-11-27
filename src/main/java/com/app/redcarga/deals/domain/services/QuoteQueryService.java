package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.QuoteItem;

import java.util.List;
import java.util.Optional;

public interface QuoteQueryService {
    List<Quote> listByRequestIdAndState(Integer requestId, String stateCode);
    List<Quote> listByCompanyIdAndState(Integer companyId, String stateCode);
    Optional<Quote> getById(Integer quoteId);
    List<QuoteItem> listItemsByQuoteId(Integer quoteId);

    List<Quote> listByRequestIdsAndState(List<Integer> requestIds, String stateCode);
}
