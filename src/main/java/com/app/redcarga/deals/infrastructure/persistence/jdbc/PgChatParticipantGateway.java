package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import com.app.redcarga.deals.application.internal.gateways.ChatParticipantGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public boolean exists(int quoteId, int userId) {
        Boolean b = jdbc.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM deals.chat_participant WHERE quote_id=? AND user_id=?)",
            Boolean.class, quoteId, userId
        );
        return Boolean.TRUE.equals(b);
    }

    @Override
    public List<Integer> findQuoteIdsByUserId(int userId) {
        return jdbc.queryForList(
            "SELECT quote_id FROM deals.chat_participant WHERE user_id = ? ORDER BY joined_at DESC",
            Integer.class, userId
        );
    }

    @Override
    public List<ParticipantRecord> findParticipantsByQuoteId(int quoteId) {
        var sql = """
            SELECT cp.user_id, q.company_id
            FROM deals.chat_participant cp
            INNER JOIN deals.quote q ON q.quote_id = cp.quote_id
            WHERE cp.quote_id = ?
            """;
        return jdbc.query(sql, (rs, rowNum) -> 
            new ParticipantRecord(rs.getInt("user_id"), rs.getInt("company_id")),
            quoteId
        );
    }
}
