package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;

public interface QuoteCommandService {
    /**
     * Create a new Quote. creatorAccountId must be resolved from the caller's JWT.
     * Returns the created quote id.
     */
    Integer create(CreateQuoteCommand cmd, Integer creatorAccountId);

    /** Update the quantity of an item within a quote. */
    void updateItemQty(Integer quoteId, Integer requestItemId, java.math.BigDecimal qty, Integer actorAccountId);

    /** Remove an item from a quote. */
    void removeItem(Integer quoteId, Integer requestItemId, Integer actorAccountId);

    /** Start negotiation: transition PENDIENTE -> TRATO. If-Match optimistic version must match. */
    void startNegotiation(Integer quoteId, Integer actorAccountId, Integer ifMatchVersion, String idempotencyKey);

    /** Reject a quote. rejectedBy must be resolved from the caller's JWT. */
    void rejectQuote(Integer quoteId, Integer rejectedBy);

}
