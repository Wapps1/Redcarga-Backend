package com.app.redcarga.planning.infrastructure.persistence.jdbc;

import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import com.app.redcarga.planning.domain.queries.ProviderRoutesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgProviderRoutesQueryRepository implements ProviderRoutesQueryRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private static final RowMapper<ProviderRouteView> MAPPER = (rs, n) -> new ProviderRouteView(
            rs.getInt("route_id"),
            rs.getInt("company_id"),
            rs.getString("company_name"),
            rs.getString("route_type"),
            rs.getString("origin_department_code"),
            rs.getString("origin_province_code"),
            rs.getString("dest_department_code"),
            rs.getString("dest_province_code"),
            rs.getString("origin_department_name"),
            rs.getString("origin_province_name"),
            rs.getString("dest_department_name"),
            rs.getString("dest_province_name"),
            rs.getBoolean("is_active")
    );

    @Override
    public List<ProviderRouteView> findByCompany(int companyId) {
        var sql = """
            SELECT r.route_id, r.company_id, c.legal_name AS company_name,
                   rt.code AS route_type,
                   r.origin_department_code, r.origin_province_code,
                   r.dest_department_code,   r.dest_province_code,
                   od.department_name AS origin_department_name,
                   op.province_name   AS origin_province_name,
                   dd.department_name AS dest_department_name,
                   dp.province_name   AS dest_province_name,
                   r.is_active
            FROM planning.provider_routes r
            JOIN providers.companies c   ON c.company_id = r.company_id
            JOIN planning.route_types rt ON rt.route_type_id = r.route_type_id
            JOIN geo.departments od      ON od.department_code = r.origin_department_code
            LEFT JOIN geo.provinces op   ON op.province_code  = r.origin_province_code
            JOIN geo.departments dd      ON dd.department_code = r.dest_department_code
            LEFT JOIN geo.provinces dp   ON dp.province_code  = r.dest_province_code
            WHERE r.company_id = :companyId
            ORDER BY r.origin_department_code, r.origin_province_code,
                     r.dest_department_code,   r.dest_province_code, r.route_id
        """;
        var params = new MapSqlParameterSource().addValue("companyId", companyId);
        return jdbc.query(sql, params, MAPPER);
    }

    @Override
    public Optional<ProviderRouteView> findByCompanyAndId(int companyId, int routeId) {
        var sql = """
            SELECT r.route_id, r.company_id, c.legal_name AS company_name,
                   rt.code AS route_type,
                   r.origin_department_code, r.origin_province_code,
                   r.dest_department_code,   r.dest_province_code,
                   od.department_name AS origin_department_name,
                   op.province_name   AS origin_province_name,
                   dd.department_name AS dest_department_name,
                   dp.province_name   AS dest_province_name,
                   r.is_active
            FROM planning.provider_routes r
            JOIN providers.companies c   ON c.company_id = r.company_id
            JOIN planning.route_types rt ON rt.route_type_id = r.route_type_id
            JOIN geo.departments od      ON od.department_code = r.origin_department_code
            LEFT JOIN geo.provinces op   ON op.province_code  = r.origin_province_code
            JOIN geo.departments dd      ON dd.department_code = r.dest_department_code
            LEFT JOIN geo.provinces dp   ON dp.province_code  = r.dest_province_code
            WHERE r.company_id = :companyId
              AND r.route_id   = :routeId
            LIMIT 1
        """;
        var params = new MapSqlParameterSource()
                .addValue("companyId", companyId)
                .addValue("routeId", routeId);
        var list = jdbc.query(sql, params, MAPPER);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}