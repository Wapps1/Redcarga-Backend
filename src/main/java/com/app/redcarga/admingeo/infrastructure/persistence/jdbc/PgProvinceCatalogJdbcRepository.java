package com.app.redcarga.admingeo.infrastructure.persistence.jdbc;

import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceEntry;
import com.app.redcarga.admingeo.domain.repositories.ProvinceCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PgProvinceCatalogJdbcRepository implements ProvinceCatalogRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<ProvinceEntry> findAllSortedByName() {
        final String sql = """
                SELECT p.province_code, p.department_code, p.province_name
                  FROM geo.provinces p
                 ORDER BY p.province_name ASC
                """;
        return jdbc.query(sql, (rs, n) ->
                new ProvinceEntry(
                        ProvinceCode.of(rs.getString("province_code")),
                        DepartmentCode.of(rs.getString("department_code")),
                        rs.getString("province_name").trim()
                )
        );
    }

    @Override
    public List<ProvinceEntry> findByDepartment(DepartmentCode departmentCode) {
        final String sql = """
                SELECT p.province_code, p.department_code, p.province_name
                  FROM geo.provinces p
                 WHERE p.department_code = :dd
                 ORDER BY p.province_name ASC
                """;
        return jdbc.query(sql, Map.of("dd", departmentCode.getValue()), (rs, n) ->
                new ProvinceEntry(
                        ProvinceCode.of(rs.getString("province_code")),
                        DepartmentCode.of(rs.getString("department_code")),
                        rs.getString("province_name").trim()
                )
        );
    }

    @Override
    public boolean exists(ProvinceCode code) {
        final String sql = """
                SELECT EXISTS(
                    SELECT 1
                      FROM geo.provinces
                     WHERE province_code = :code
                )
                """;
        Boolean ok = jdbc.queryForObject(sql, Map.of("code", code.getValue()), Boolean.class);
        return Boolean.TRUE.equals(ok);
    }
}
