package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;

public interface DocsCommandService {
    void markDocGreRemitente(Integer quoteId, Integer actorAccountId);
    void markDocGreTransportista(Integer quoteId, Integer actorAccountId);
    
    Guide createGuide(GuideType type, Integer quoteId, String guideUrl);
    Guide updateGuideUrl(Integer guideId, String guideUrl);
}
