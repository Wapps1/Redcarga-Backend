package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;

public interface QuoteCommandService {
    /**
     * Create a new Quote. creatorAccountId must be resolved from the caller's JWT.
     * Returns the created quote id.
     */
    Integer create(CreateQuoteCommand cmd, Integer creatorAccountId);
}
