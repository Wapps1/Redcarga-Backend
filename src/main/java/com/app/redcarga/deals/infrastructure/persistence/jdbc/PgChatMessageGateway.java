package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class PgChatMessageGateway implements ChatMessageGateway {

    private final NamedParameterJdbcTemplate jdbc;

    private static final RowMapper<ChatMessageDto> MAPPER = (rs, n) -> new ChatMessageDto(
        rs.getInt("chat_message_id"),
        rs.getInt("quote_id"),
        rs.getString("type_code"),
        rs.getString("content_code"),
        rs.getString("body"),
        rs.getString("media_url"),
        Optional.ofNullable(rs.getString("client_dedup_key")).map(java.util.UUID::fromString).orElse(null),
        rs.getInt("created_by"),
        rs.getTimestamp("created_at").toInstant(),
        rs.getString("system_subtype_code"),
        null // info se completa al serializar al WS si corresponde
    );

    @Override
    public Optional<ChatMessageDto> findByDedupKey(int quoteId, UUID dedupKey) {
    if (dedupKey == null) return Optional.empty();

    var sql = "select chat_message_id, quote_id, type_code, content_code, body, media_url, client_dedup_key, created_by, created_at, system_subtype_code " +
        "from deals.chat_message where quote_id=:q and client_dedup_key = CAST(:k AS uuid)";
    var params = Map.of("q", quoteId, "k", dedupKey.toString());
    var list = jdbc.query(sql, params, MAPPER);
    return list.stream().findFirst();
    }

    @Override
    public int insertUserMessage(int quoteId, String contentCode, String body, String mediaUrl, UUID dedupKey, int createdBy) {
        var sql = """
            INSERT INTO deals.chat_message
            (quote_id, type_code, content_code, body, media_url, client_dedup_key, created_by, created_at)
            VALUES (:q,'USER',:cc,:b,:mu,:dk,:cb, now())
            RETURNING chat_message_id
            """;
        var params = new HashMap<String,Object>();
        params.put("q", quoteId);
        params.put("cc", contentCode);
        params.put("b", body);
        params.put("mu", mediaUrl);
        params.put("dk", dedupKey != null ? dedupKey : null);
        params.put("cb", createdBy);
        return jdbc.queryForObject(sql, params, Integer.class);
    }

    @Override
    public int insertSystemMessage(int quoteId, String systemSubtypeCode, Integer refChangeId, String body, int createdBy) {
        var sql = """
            INSERT INTO deals.chat_message
            (quote_id, type_code, system_subtype_code, ref_change_id, body, created_by, created_at)
            VALUES (:q,'SYSTEM',:ssc,:rc,:b,:cb, now())
            RETURNING chat_message_id
            """;
        var params = new HashMap<String,Object>();
        params.put("q", quoteId);
        params.put("ssc", systemSubtypeCode);
        params.put("rc", refChangeId);
        params.put("b", body == null ? "" : body);
        params.put("cb", createdBy);
        return jdbc.queryForObject(sql, params, Integer.class);
    }

    @Override
    public List<ChatMessageDto> findAfter(int quoteId, int afterId, int limit) {
        var sql = """
            select chat_message_id, quote_id, type_code, content_code, body, media_url, client_dedup_key, created_by, created_at, system_subtype_code
            from deals.chat_message
            where quote_id=:q and chat_message_id > :a
            order by chat_message_id asc
            limit :l
            """;
        return jdbc.query(sql, Map.of("q", quoteId, "a", afterId, "l", limit), MAPPER);
    }

    @Override
    public int findMaxMessageId(int quoteId) {
        Integer v = jdbc.queryForObject("select coalesce(max(chat_message_id),0) from deals.chat_message where quote_id=:q",
                Map.of("q", quoteId), Integer.class);
        return v == null ? 0 : v;
    }
}
