package com.app.redcarga.deals.domain.services;

public interface AcceptanceCommandService {
    Integer proposeAcceptance(Integer quoteId, Integer actorAccountId, String idempotencyKey, String note);
    void confirmAcceptance(Integer quoteId, Integer acceptanceId, Integer resolverAccountId);
    void rejectAcceptance(Integer quoteId, Integer acceptanceId, Integer resolverAccountId);
}
