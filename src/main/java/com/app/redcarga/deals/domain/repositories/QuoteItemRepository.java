package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.QuoteItem;

import java.util.List;

public interface QuoteItemRepository {
    List<QuoteItem> findByQuoteId(Integer quoteId);
}
