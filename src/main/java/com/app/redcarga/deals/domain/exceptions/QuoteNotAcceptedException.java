package com.app.redcarga.deals.domain.exceptions;

public class QuoteNotAcceptedException extends RuntimeException {
    private final Integer quoteId;

    public QuoteNotAcceptedException(Integer quoteId) {
        super("Quote " + quoteId + " is not in ACEPTADA state");
        this.quoteId = quoteId;
    }

    public Integer getQuoteId() {
        return quoteId;
    }
}
