package com.app.redcarga.deals.application.internal.gateways;

/**
 * Gateway used by application services to ensure a chat participant row exists for a quote.
 */
public interface ChatParticipantGateway {
    /** Ensure the participant (quoteId,userId) exists (idempotent).
     * Implementations should use INSERT ... ON CONFLICT DO NOTHING or equivalent.
     */
    void ensure(int quoteId, int userId);
}
