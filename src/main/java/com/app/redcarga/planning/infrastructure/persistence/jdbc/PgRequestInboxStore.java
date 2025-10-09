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
                    (request_id, company_id, matched_route_id, route_type_id, status, created_at,
                     requester_name, origin_department_name, origin_province_name, dest_department_name, dest_province_name, total_quantity)
                values
                    (:request_id, :company_id, :route_id, :route_type_id, 'OPEN', :created_at,
                     :requester_name, :origin_department_name, :origin_province_name, :dest_department_name, :dest_province_name, :total_quantity)
                on conflict (request_id, company_id) do nothing
                """;

    @Override
    public void insertIfAbsent(int requestId,
                   int companyId,
                   int routeId,
                   int routeTypeId,
                   Instant createdAt,
                   String requesterName,
                   String originDepartmentName,
                   String originProvinceName,
                   String destDepartmentName,
                   String destProvinceName,
                   Integer totalQuantity) {

    var params = new MapSqlParameterSource()
        .addValue("request_id", requestId)
        .addValue("company_id", companyId)
        .addValue("route_id", routeId)
        .addValue("route_type_id", routeTypeId)
        .addValue("created_at", Timestamp.from(createdAt))
        .addValue("requester_name", requesterName)
        .addValue("origin_department_name", originDepartmentName)
        .addValue("origin_province_name", originProvinceName)
        .addValue("dest_department_name", destDepartmentName)
        .addValue("dest_province_name", destProvinceName)
        .addValue("total_quantity", totalQuantity);

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
