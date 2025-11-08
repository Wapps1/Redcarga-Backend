package com.app.redcarga.deals.interfaces.rest.requests;

import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.deals.domain.model.valueobjects.Currency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateQuoteRequest(
        @NotNull Integer requestId,
        @NotNull Integer companyId,
        @NotNull @DecimalMin(value = "0.01", message = "totalAmount_positive") BigDecimal totalAmount,
        Currency currency,
        @Valid @NotEmpty(message = "items_required") List<QuoteItemRequest> items
) {
    public record QuoteItemRequest(
            @NotNull Integer requestItemId,
            @NotNull @DecimalMin(value = "0.0001", message = "qty_positive") BigDecimal qty
    ) {}

    public CreateQuoteCommand toCommand() {
        var drafts = items.stream()
                .map(i -> new CreateQuoteCommand.QuoteItemDraft(i.requestItemId(), i.qty()))
                .toList();
        return new CreateQuoteCommand(requestId, companyId, totalAmount, currency, drafts);
    }
}
