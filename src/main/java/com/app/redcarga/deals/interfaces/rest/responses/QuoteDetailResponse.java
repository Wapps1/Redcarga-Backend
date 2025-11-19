package com.app.redcarga.deals.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record QuoteDetailResponse(
        Integer quoteId,
        Integer requestId,
        Integer companyId,
        Integer createdByAccountId,
        String stateCode,
        String currencyCode,
        BigDecimal totalAmount,
        Integer version,
        Instant createdAt,
        Instant updatedAt,
        List<QuoteItemResponse> items
) {}
