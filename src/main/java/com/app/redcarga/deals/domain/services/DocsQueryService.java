package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;

import java.util.List;
import java.util.Optional;

public interface DocsQueryService {
    List<Guide> getGuidesByQuoteId(Integer quoteId);
    Optional<Guide> getGuideByQuoteIdAndType(Integer quoteId, GuideType type);
}
