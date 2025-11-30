package com.app.redcarga.deals.domain.repositories;

import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;

import java.util.List;
import java.util.Optional;

public interface GuideRepository {
    Guide save(Guide guide);
    Optional<Guide> findById(Integer guideId);
    List<Guide> findByQuoteId(Integer quoteId);
    Optional<Guide> findByQuoteIdAndType(Integer quoteId, GuideType type);
    boolean existsByQuoteIdAndType(Integer quoteId, GuideType type);
}
