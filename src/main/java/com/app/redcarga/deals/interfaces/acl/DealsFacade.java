package com.app.redcarga.deals.interfaces.acl;

/**
 * Facade público de deals p
 */
public interface DealsFacade {
    boolean isChatParticipant(Integer quoteId, Integer accountId);

    boolean isDriverOfQuote(Integer quoteId, Integer accountId);
}