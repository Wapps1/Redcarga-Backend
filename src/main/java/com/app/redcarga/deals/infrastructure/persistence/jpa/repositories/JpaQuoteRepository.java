package com.app.redcarga.deals.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaQuoteRepository extends JpaRepository<Quote, Integer>, QuoteRepository {

    @Override
    Optional<Quote> findById(Integer id);

    List<Quote> findByRequestIdAndStateCode(Integer requestId, String stateCode);

    List<Quote> findByRequestId(Integer requestId);
}