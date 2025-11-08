package com.app.redcarga.deals.domain.model.commands;

import com.app.redcarga.deals.domain.model.valueobjects.Currency;
import java.math.BigDecimal;

public record CreateQuoteCommand(
    Integer requestId,
    Integer providerId,
    BigDecimal totalAmount,
    Currency currency
) {}
