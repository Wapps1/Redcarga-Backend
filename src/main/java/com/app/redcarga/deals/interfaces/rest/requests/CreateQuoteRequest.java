package com.app.redcarga.deals.interfaces.rest.requests;

import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.deals.domain.model.valueobjects.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateQuoteRequest(
        @NotNull Integer requestId,
        @NotNull Integer providerId,
        @NotNull @Min(1) BigDecimal totalAmount,
        Currency currency
) {
    public CreateQuoteCommand toCommand() {
        return new CreateQuoteCommand(requestId, providerId, totalAmount, currency);
    }
}
