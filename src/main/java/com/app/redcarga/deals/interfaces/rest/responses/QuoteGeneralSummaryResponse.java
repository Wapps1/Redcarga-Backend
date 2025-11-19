package com.app.redcarga.deals.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.Instant;

public record QuoteGeneralSummaryResponse(
        Integer quoteId,
        Integer requestId,
        Integer companyId,
        BigDecimal totalAmount,
        String currencyCode,
        Instant createdAt
) {}
