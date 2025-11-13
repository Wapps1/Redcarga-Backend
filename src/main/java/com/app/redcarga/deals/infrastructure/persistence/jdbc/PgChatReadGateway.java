package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import com.app.redcarga.deals.application.internal.gateways.ChatReadGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgChatReadGateway implements ChatReadGateway {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Integer> findLastSeen(int quoteId, int userId) {
        var sql = "select last_seen_message_id from deals.chat_read where quote_id=:q and user_id=:u";
        var list = jdbc.query(sql, Map.of("q", quoteId, "u", userId),
                (rs, n) -> rs.getInt("last_seen_message_id"));
        return list.stream().findFirst();
    }

    @Override
    public void upsertLastSeen(int quoteId, int userId, int lastSeenMessageId) {
        var sql = """
            INSERT INTO deals.chat_read (quote_id, user_id, last_seen_message_id, updated_at)
            VALUES (:q,:u,:l, now())
            ON CONFLICT (quote_id, user_id)
            DO UPDATE SET last_seen_message_id = GREATEST(deals.chat_read.last_seen_message_id, EXCLUDED.last_seen_message_id),
                          updated_at = now()
            """;
        jdbc.update(sql, Map.of("q", quoteId, "u", userId, "l", lastSeenMessageId));
    }
}
