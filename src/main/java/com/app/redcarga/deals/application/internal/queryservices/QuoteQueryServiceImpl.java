package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.QuoteItem;
import com.app.redcarga.deals.domain.repositories.QuoteItemRepository;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteQueryServiceImpl implements QuoteQueryService {

    private final QuoteRepository quoteRepository;
    private final QuoteItemRepository quoteItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Quote> listByRequestIdAndState(Integer requestId, String stateCode) {
        if (stateCode != null && !stateCode.isBlank()) {
            return quoteRepository.findByRequestIdAndStateCode(requestId, stateCode);
        }
        return quoteRepository.findByRequestId(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quote> listByCompanyIdAndState(Integer companyId, String stateCode) {
        if (stateCode == null || stateCode.isBlank()) {
            return quoteRepository.findByCompanyId(companyId);
        }
        String normalized = stateCode.trim().toUpperCase(java.util.Locale.ROOT);
        if ("TRATO".equals(normalized)) {
            return quoteRepository.findByCompanyIdAndStateCodeIn(companyId, java.util.List.of("TRATO", "EN_ESPERA"));
        }
        return quoteRepository.findByCompanyIdAndStateCode(companyId, normalized);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Quote> getById(Integer quoteId) {
        return quoteRepository.findById(quoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteItem> listItemsByQuoteId(Integer quoteId) {
        return quoteItemRepository.findByQuoteId(quoteId);
    }
}
