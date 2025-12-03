package com.app.redcarga.tracking.domain.repositories;

import com.app.redcarga.tracking.domain.model.aggregates.CurrentQuoteLocation;

import java.util.Optional;

public interface CurrentQuoteLocationRepository {
    Optional<CurrentQuoteLocation> findByQuoteId(Integer quoteId);
    CurrentQuoteLocation save(CurrentQuoteLocation entity);
}
