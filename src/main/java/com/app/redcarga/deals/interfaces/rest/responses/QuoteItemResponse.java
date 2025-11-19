package com.app.redcarga.deals.interfaces.rest.responses;

import java.math.BigDecimal;

public record QuoteItemResponse(
        Integer quoteItemId,
        Integer requestItemId,
        BigDecimal qty,
        Integer version
) {}
