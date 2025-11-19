package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.entities.QuoteItem;
import com.app.redcarga.deals.domain.repositories.QuoteItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaQuoteItemRepository extends JpaRepository<QuoteItem, Integer>, QuoteItemRepository {
    // Spring Data derived query using nested property
    List<QuoteItem> findByQuote_Id(Integer quoteId);

    @Override
    default List<QuoteItem> findByQuoteId(Integer quoteId) {
        return findByQuote_Id(quoteId);
    }
}
