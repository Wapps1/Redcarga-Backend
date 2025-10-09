package com.app.redcarga.planning.infrastructure.persistence.jdbc;

import com.app.redcarga.planning.application.internal.gateways.RequestInboxQueryRepository;
import com.app.redcarga.planning.application.internal.views.RequestInboxEntryView;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PgRequestInboxQueryRepository implements RequestInboxQueryRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private static final String SQL = """
        select request_id, company_id, matched_route_id, route_type_id, status, created_at,
               requester_name, origin_department_name, origin_province_name,
               dest_department_name, dest_province_name, total_quantity
        from planning.request_inbox
        where company_id = :companyId
        order by created_at desc
        """;

    @Override
    public List<RequestInboxEntryView> findByCompany(int companyId) {
        var params = new MapSqlParameterSource().addValue("companyId", companyId);
        return jdbc.query(SQL, params, (rs, rowNum) -> new RequestInboxEntryView(
            rs.getInt("request_id"),
            rs.getInt("company_id"),
            rs.getObject("matched_route_id", Integer.class),
            rs.getObject("route_type_id", Integer.class),
            rs.getString("status"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : Instant.EPOCH,
            rs.getString("requester_name"),
            rs.getString("origin_department_name"),
            rs.getString("origin_province_name"),
            rs.getString("dest_department_name"),
            rs.getString("dest_province_name"),
            rs.getObject("total_quantity", Integer.class)
        ));
    }
}