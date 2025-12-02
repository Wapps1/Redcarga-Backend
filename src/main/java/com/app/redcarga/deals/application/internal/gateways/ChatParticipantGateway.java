package com.app.redcarga.deals.application.internal.gateways;

import java.util.List;

/**
 * Gateway used by application services to ensure a chat participant row exists for a quote.
 */
public interface ChatParticipantGateway {
    /** Ensure the participant (quoteId,userId) exists (idempotent).
     * Implementations should use INSERT ... ON CONFLICT DO NOTHING or equivalent.
     */
    void ensure(int quoteId, int userId);

    /** Check if (quoteId,userId) is a participant. */
    boolean exists(int quoteId, int userId);

    /** Get all quote IDs where the user is a participant. */
    List<Integer> findQuoteIdsByUserId(int userId);

    /** Get all participants for a quote with company info from quote table. */
    List<ParticipantRecord> findParticipantsByQuoteId(int quoteId);

    record ParticipantRecord(Integer userId, Integer companyId) {}
}
