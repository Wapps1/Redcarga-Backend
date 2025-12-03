package com.app.redcarga.tracking.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.tracking.domain.model.aggregates.CurrentQuoteLocation;
import com.app.redcarga.tracking.domain.repositories.CurrentQuoteLocationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCurrentQuoteLocationRepository
        extends JpaRepository<CurrentQuoteLocation, Integer>, CurrentQuoteLocationRepository {

    @Override
    default Optional<CurrentQuoteLocation> findByQuoteId(Integer quoteId) {
        return findById(quoteId);
    }

}
