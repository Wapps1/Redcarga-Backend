package com.app.redcarga.deals.interfaces.rest.responses;

import java.math.BigDecimal;

public record QuoteSummaryResponse(
        Integer id,
        Integer requestId,
        Integer companyId,
        String stateCode,
        String currency,
        BigDecimal totalAmount,
        Integer version
) {}
