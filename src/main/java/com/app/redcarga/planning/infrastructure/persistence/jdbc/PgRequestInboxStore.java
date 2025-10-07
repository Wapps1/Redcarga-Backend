package com.app.redcarga.planning.infrastructure.persistence.jdbc;

import com.app.redcarga.planning.application.internal.gateways.RequestInboxStore;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Inserción idempotente en planning.request_inbox.
 * ON CONFLICT (request_id, company_id) DO NOTHING
 */
@Component
@RequiredArgsConstructor
public class PgRequestInboxStore implements RequestInboxStore {

    private final NamedParameterJdbcTemplate jdbc;

    private static final String SQL_INSERT = """
        insert into planning.request_inbox
          (request_id, company_id, matched_route_id, route_type_id, status, created_at)
        values
          (:request_id, :company_id, :route_id, :route_type_id, 'OPEN', :created_at)
        on conflict (request_id, company_id) do nothing
        """;

    @Override
    public void insertIfAbsent(int requestId,
                               int companyId,
                               int routeId,
                               int routeTypeId,
                               Instant createdAt) {

        var params = new MapSqlParameterSource()
                .addValue("request_id", requestId)
                .addValue("company_id", companyId)
                .addValue("route_id", routeId)
                .addValue("route_type_id", routeTypeId)
                .addValue("created_at", Timestamp.from(createdAt));

        jdbc.update(SQL_INSERT, params);
    }

    @Override
    public List<Integer> closeAllForRequest(int requestId) {
        // 1) Obtiene companies afectadas (distintas)
        var companies = jdbc.queryForList("""
            select distinct company_id
            from planning.request_inbox
            where request_id = :rid and status = 'OPEN'
            """,
                Map.of("rid", requestId), Integer.class);

        if (companies.isEmpty()) return companies;

        // 2) Cierra
        jdbc.update("""
            update planning.request_inbox
            set status = 'CLOSED'
            where request_id = :rid and status = 'OPEN'
            """, Map.of("rid", requestId));

        return companies;
    }
}
