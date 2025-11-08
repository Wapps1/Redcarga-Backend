package com.app.redcarga.deals.domain.model.commands;

import com.app.redcarga.deals.domain.model.valueobjects.Currency;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public record CreateQuoteCommand(
    Integer requestId,
    Integer companyId,
    BigDecimal totalAmount,
    Currency currency,
    List<QuoteItemDraft> items
) {
    public record QuoteItemDraft(Integer requestItemId, java.math.BigDecimal qty) {}
}
