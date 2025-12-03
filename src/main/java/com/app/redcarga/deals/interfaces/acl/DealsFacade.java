package com.app.redcarga.deals.interfaces.acl;

import java.util.Optional;

/**
 * Facade público de deals
 */
public interface DealsFacade {
    boolean isChatParticipant(Integer quoteId, Integer accountId);

    boolean isDriverOfQuote(Integer quoteId, Integer accountId);

    Optional<Integer> findDriverIdByQuoteId(Integer quoteId);
}