package com.app.redcarga.planning.infrastructure.persistence.jdbc;

import com.app.redcarga.planning.application.internal.gateways.ProviderRouteMatchingGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Implementación JDBC del matching PP ∪ DD con dedupe por company (PP tiene prioridad).
 * Requiere el record:
 *   ProviderRouteMatchingGateway.Candidate(int routeId, int companyId, int routeTypeId)
 * definido en el puerto de application.
 */
@Component
@RequiredArgsConstructor
public class PgProviderRouteMatchingGateway implements ProviderRouteMatchingGateway {

    private final NamedParameterJdbcTemplate jdbc;

    private static final RowMapper<ProviderRouteMatchingGateway.Candidate> CANDIDATE_MAPPER =
            new RowMapper<>() {
                @Override
                public ProviderRouteMatchingGateway.Candidate mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new ProviderRouteMatchingGateway.Candidate(
                            rs.getInt("route_id"),
                            rs.getInt("company_id"),
                            rs.getInt("route_type_id")
                    );
                }
            };

    @Override
    public List<ProviderRouteMatchingGateway.Candidate> findBestPerCompany(
            String originProvinceCode,
            String originDepartmentCode,
            String destProvinceCode,
            String destDepartmentCode
    ) {
        // Nota: PP y DD usan t.code para evitar acoplar IDs de catálogo.
        final String sql = """
            with candidates as (
                -- PP exacto
                select r.route_id, r.company_id, r.route_type_id, 1 as prio
                from planning.provider_routes r
                join planning.route_types t on t.route_type_id = r.route_type_id and t.code = 'PP'
                where r.is_active
                  and r.origin_province_code = :o_prov
                  and r.dest_province_code   = :d_prov

                union all

                -- DD por departamentos
                select r.route_id, r.company_id, r.route_type_id, 2 as prio
                from planning.provider_routes r
                join planning.route_types t on t.route_type_id = r.route_type_id and t.code = 'DD'
                where r.is_active
                  and r.origin_department_code = :o_dep
                  and r.dest_department_code   = :d_dep
            ),
            ranked as (
                select route_id, company_id, route_type_id,
                       row_number() over (partition by company_id order by prio asc) as rn
                from candidates
            )
            select route_id, company_id, route_type_id
            from ranked
            where rn = 1
            """;

        var params = Map.of(
                "o_prov", originProvinceCode,
                "d_prov", destProvinceCode,
                "o_dep",  originDepartmentCode,
                "d_dep",  destDepartmentCode
        );

        return jdbc.query(sql, params, CANDIDATE_MAPPER);
    }
}
