package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;
import com.app.redcarga.deals.domain.repositories.GuideRepository;
import com.app.redcarga.deals.domain.services.DocsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocsQueryServiceImpl implements DocsQueryService {

    private final GuideRepository guideRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Guide> getGuidesByQuoteId(Integer quoteId) {
        return guideRepository.findByQuoteId(quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Guide> getGuideByQuoteIdAndType(Integer quoteId, GuideType type) {
        return guideRepository.findByQuoteIdAndType(quoteId, type);
    }
}
