package com.app.redcarga.deals.application.internal.queryservices;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.repositories.QuoteRepository;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteQueryServiceImpl implements QuoteQueryService {

    private final QuoteRepository quoteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Quote> listByRequestIdAndState(Integer requestId, String stateCode) {
        if (stateCode != null && !stateCode.isBlank()) {
            return quoteRepository.findByRequestIdAndStateCode(requestId, stateCode);
        }
        return quoteRepository.findByRequestId(requestId);
    }
}
