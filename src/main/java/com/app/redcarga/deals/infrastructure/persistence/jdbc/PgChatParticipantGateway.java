package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PgChatParticipantGateway implements ChatParticipantGateway {
    private final JdbcTemplate jdbc;

    @Override
    public void ensure(int quoteId, int userId) {
        jdbc.update(
            "INSERT INTO deals.chat_participant (quote_id, user_id) VALUES (?, ?) ON CONFLICT (quote_id, user_id) DO NOTHING",
            quoteId, userId
        );
    }
}
