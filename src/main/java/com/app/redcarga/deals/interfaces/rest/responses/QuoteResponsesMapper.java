package com.app.redcarga.deals.interfaces.rest.responses;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import com.app.redcarga.deals.domain.model.entities.QuoteItem;

import java.util.List;

public final class QuoteResponsesMapper {
    private QuoteResponsesMapper() {}

        public static QuoteGeneralSummaryResponse toGeneralSummary(Quote q) {
                return new QuoteGeneralSummaryResponse(
                                q.getId(),
                                q.getRequestId(),
                                q.getCompanyId(),
                                q.getTotalAmount(),
                                q.getCurrency().name(),
                                q.getCreatedAt() != null ? q.getCreatedAt().toInstant() : null
                );
        }

    public static QuoteDetailResponse toDetail(Quote q, List<QuoteItem> items) {
        List<QuoteItemResponse> itemDtos = items.stream()
                .map(i -> new QuoteItemResponse(
                        i.getId(),
                        i.getRequestItemId(),
                        i.getQty(),
                        i.getVersion()
                ))
                .toList();
        return new QuoteDetailResponse(
                q.getId(),
                q.getRequestId(),
                q.getCompanyId(),
                q.getCreatedByAccountId(),
                q.getStateCode(),
                q.getCurrency().name(),
                q.getTotalAmount(),
                q.getVersion(),
                q.getCreatedAt() != null ? q.getCreatedAt().toInstant() : null,
                q.getUpdatedAt() != null ? q.getUpdatedAt().toInstant() : null,
                itemDtos
        );
    }
}
