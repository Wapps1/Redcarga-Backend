package com.app.redcarga.deals.infrastructure.persistence.jdbc;

import com.app.redcarga.deals.application.internal.gateways.ChatMessageGateway;
import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;
import com.app.redcarga.deals.domain.model.entities.AcceptanceProposal;
import com.app.redcarga.deals.domain.repositories.AcceptanceProposalRepository;
import com.app.redcarga.deals.infrastructure.persistence.jpa.repositories.JpaChangeRepository;
import com.app.redcarga.deals.interfaces.rest.responses.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class PgChatMessageGateway implements ChatMessageGateway {

    private final NamedParameterJdbcTemplate jdbc;
    private final AcceptanceProposalRepository acceptanceRepo;
    private final JpaChangeRepository changeRepo;

    @Override
    public Optional<ChatMessageDto> findByDedupKey(int quoteId, UUID dedupKey) {
    if (dedupKey == null) return Optional.empty();
        var sql = "select chat_message_id, quote_id, type_code, content_code, body, media_url, client_dedup_key, created_by, created_at, system_subtype_code, ref_change_id, ref_acceptance_id " +
            "from deals.chat_message where quote_id=:q and client_dedup_key = CAST(:k AS uuid)";
        var params = Map.of("q", quoteId, "k", dedupKey.toString());
        var list = jdbc.query(sql, params, this::mapRowToDto);
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
    public int insertSystemMessage(int quoteId, String systemSubtypeCode, Integer refChangeId, Integer refAcceptanceId, String body, int createdBy) {
        String contentCode = "TEXT";

        String sql = """
            INSERT INTO deals.chat_message
              (quote_id, type_code, content_code, body, created_by, created_at, system_subtype_code, ref_change_id, ref_acceptance_id)
            VALUES (:quoteId, 'SYSTEM', :contentCode, :body, :createdBy, :createdAt, :systemSubtype, :refChangeId, :refAcceptanceId)
            RETURNING chat_message_id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("quoteId", quoteId)
            .addValue("contentCode", contentCode)
            .addValue("body", body)
            .addValue("createdBy", createdBy)
            .addValue("createdAt", java.sql.Timestamp.from(java.time.Instant.now()))
            .addValue("systemSubtype", systemSubtypeCode)
            // Determinar en qué columna poner la FK según el subtype
            .addValue("refChangeId", (systemSubtypeCode != null && systemSubtypeCode.startsWith("CHANGE")) ? refChangeId : null)
            .addValue("refAcceptanceId", (systemSubtypeCode != null && systemSubtypeCode.startsWith("ACCEPTANCE")) ? refAcceptanceId : null);

        Integer id = jdbc.queryForObject(sql, params, Integer.class);
        return id == null ? -1 : id;
    }

    @Override
    public List<ChatMessageDto> findAfter(int quoteId, int afterId, int limit) {
        var sql = """
            select chat_message_id, quote_id, type_code, content_code, body, media_url, client_dedup_key, created_by, created_at, system_subtype_code, ref_change_id, ref_acceptance_id
            from deals.chat_message
            where quote_id=:q and chat_message_id > :a
            order by chat_message_id asc
            limit :l
            """;
        return jdbc.query(sql, Map.of("q", quoteId, "a", afterId, "l", limit), this::mapRowToDto);
    }

    @Override
    public int findMaxMessageId(int quoteId) {
        Integer v = jdbc.queryForObject("select coalesce(max(chat_message_id),0) from deals.chat_message where quote_id=:q",
                Map.of("q", quoteId), Integer.class);
        return v == null ? 0 : v;
    }

    private ChatMessageDto mapRowToDto(ResultSet rs, int rowNum) throws SQLException {
        Integer chatMessageId = rs.getInt("chat_message_id");
        Integer quoteId = rs.getInt("quote_id");
        String typeCode = rs.getString("type_code");
        String contentCode = rs.getString("content_code");
        String body = rs.getString("body");
        String mediaUrl = rs.getString("media_url");
        UUID clientDedup = Optional.ofNullable(rs.getString("client_dedup_key")).map(UUID::fromString).orElse(null);
        int createdBy = rs.getInt("created_by");
        java.time.Instant createdAt = rs.getTimestamp("created_at").toInstant();
        String systemSubtype = rs.getString("system_subtype_code");
        Integer refChangeId = rs.getObject("ref_change_id") == null ? null : rs.getInt("ref_change_id");
        Integer refAcceptanceId = rs.getObject("ref_acceptance_id") == null ? null : rs.getInt("ref_acceptance_id");

        Object info = null;
        if ("SYSTEM".equals(typeCode) && systemSubtype != null) {
            try {
                if (systemSubtype.startsWith("ACCEPTANCE") && refAcceptanceId != null) {
                    Optional<AcceptanceProposal> ap = acceptanceRepo.findById(refAcceptanceId);
                    if (ap.isPresent()) {
                        AcceptanceProposal p = ap.get();
                        Map<String, Object> m = new HashMap<>();
                        m.put("acceptanceId", p.getAcceptanceId());
                        m.put("quoteId", p.getQuoteId());
                        m.put("status", p.getStatus() != null ? p.getStatus().name() : null);
                        m.put("initiatorUserId", p.getInitiatorUserId());
                        m.put("resolverUserId", p.getResolverUserId());
                        m.put("createdAt", p.getCreatedAt());
                        m.put("resolvedAt", p.getResolvedAt());
                        m.put("idempotencyKey", p.getIdempotencyKey());
                        Map<String, Object> wrapper = Map.of("acceptance", m);
                        info = wrapper;
                    }
                } else if (systemSubtype.startsWith("CHANGE") && refChangeId != null) {
                    Optional<Change> ch = changeRepo.findById(refChangeId);
                    if (ch.isPresent()) {
                        Change c = ch.get();
                        Map<String, Object> m = new HashMap<>();
                        m.put("changeId", c.getChangeId());
                        m.put("kindCode", c.getKindCode());
                        m.put("statusCode", c.getStatusCode());
                        m.put("createdBy", c.getCreatedBy());
                        m.put("createdAt", c.getCreatedAt());
                        List<Map<String, Object>> items = new ArrayList<>();
                        for (ChangeItem it : c.getItems()) {
                            Map<String, Object> im = new HashMap<>();
                            im.put("changeItemId", it.getChangeItemId());
                            im.put("fieldCode", it.getFieldCode());
                            im.put("targetQuoteItemId", it.getTargetQuoteItemId());
                            im.put("targetRequestItemId", it.getTargetRequestItemId());
                            im.put("oldValue", it.getOldValue());
                            im.put("newValue", it.getNewValue());
                            items.add(im);
                        }
                        m.put("items", items);
                        Map<String, Object> wrapper = Map.of("change", m);
                        info = wrapper;
                    }
                }
            } catch (Exception ex) {
                // best-effort: if referenced entity cannot be loaded, leave info null
            }
        }

        return new ChatMessageDto(
            chatMessageId,
            quoteId,
            typeCode,
            contentCode,
            body,
            mediaUrl,
            clientDedup,
            createdBy,
            createdAt,
            systemSubtype,
            info
        );
    }
}
