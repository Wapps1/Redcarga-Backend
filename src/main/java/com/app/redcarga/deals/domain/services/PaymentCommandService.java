package com.app.redcarga.deals.domain.services;

public interface PaymentCommandService {
    void markPaymentMade(Integer quoteId, Integer actorAccountId);
    void markPaymentConfirmed(Integer quoteId, Integer actorAccountId);
}
