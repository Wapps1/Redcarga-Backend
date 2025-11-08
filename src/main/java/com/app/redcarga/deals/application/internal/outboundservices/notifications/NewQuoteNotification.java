package com.app.redcarga.deals.application.internal.outboundservices.notifications;

import java.math.BigDecimal;
import java.time.Instant;

public record NewQuoteNotification(
        Integer quoteId,
        Integer requestId,
        Integer companyId,
        BigDecimal totalAmount,
        String currencyCode,
        Instant createdAt
) {}
