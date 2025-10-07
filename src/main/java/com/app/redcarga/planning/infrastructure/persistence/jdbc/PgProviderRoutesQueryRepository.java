package com.app.redcarga.planning.infrastructure.persistence.jdbc;

import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import com.app.redcarga.planning.domain.queries.ProviderRoutesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
            rs.getBoolean("is_active")
    );

    @Override
    public List<ProviderRouteView> findByCompany(
            int companyId,
            String shape,
            Boolean active,
            String originDepartmentCode,
            String originProvinceCode,
            String destDepartmentCode,
            String destProvinceCode
    ) {
        StringBuilder sql = new StringBuilder("""
            SELECT r.route_id,
                   r.company_id,
                   c.legal_name AS company_name,
                   rt.code      AS route_type,
                   r.origin_department_code,
                   r.origin_province_code,
                   r.dest_department_code,
                   r.dest_province_code,
                   r.is_active
            FROM planning.provider_routes r
            JOIN providers.companies c   ON c.company_id = r.company_id
            JOIN planning.route_types rt ON rt.route_type_id = r.route_type_id
            WHERE r.company_id = :companyId
        """);

        var params = new MapSqlParameterSource().addValue("companyId", companyId);
        var conds  = new ArrayList<String>();

        if (shape != null && !shape.isBlank()) {
            conds.add("rt.code = :shape");
            params.addValue("shape", shape.trim().toUpperCase());
        }
        if (active != null) {
            conds.add("r.is_active = :active");
            params.addValue("active", active);
        }
        if (originDepartmentCode != null && !originDepartmentCode.isBlank()) {
            conds.add("r.origin_department_code = :od");
            params.addValue("od", originDepartmentCode.trim());
        }
        if (originProvinceCode != null && !originProvinceCode.isBlank()) {
            conds.add("r.origin_province_code = :op");
            params.addValue("op", originProvinceCode.trim());
        }
        if (destDepartmentCode != null && !destDepartmentCode.isBlank()) {
            conds.add("r.dest_department_code = :dd");
            params.addValue("dd", destDepartmentCode.trim());
        }
        if (destProvinceCode != null && !destProvinceCode.isBlank()) {
            conds.add("r.dest_province_code = :dp");
            params.addValue("dp", destProvinceCode.trim());
        }

        if (!conds.isEmpty()) sql.append(" AND ").append(String.join(" AND ", conds));
        sql.append(" ORDER BY r.origin_department_code, r.origin_province_code, r.dest_department_code, r.dest_province_code, r.route_id");
        return jdbc.query(sql.toString(), params, MAPPER);
    }
}
