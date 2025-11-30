package com.app.redcarga.deals.domain.exceptions;

import com.app.redcarga.deals.domain.model.valueobjects.GuideType;

public class GuideAlreadyExistsException extends RuntimeException {
    private final Integer quoteId;
    private final GuideType type;

    public GuideAlreadyExistsException(Integer quoteId, GuideType type) {
        super("Guide with type " + type + " already exists for quote " + quoteId);
        this.quoteId = quoteId;
        this.type = type;
    }

    public Integer getQuoteId() {
        return quoteId;
    }

    public GuideType getType() {
        return type;
    }
}
