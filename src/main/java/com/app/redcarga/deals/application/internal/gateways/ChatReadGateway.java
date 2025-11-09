package com.app.redcarga.deals.application.internal.gateways;

import java.util.Optional;

public interface ChatReadGateway {
    Optional<Integer> findLastSeen(int quoteId, int userId);
    void upsertLastSeen(int quoteId, int userId, int lastSeenMessageId);
}
