package com.app.redcarga.admingeo.infrastructure.persistence.jdbc;

import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentEntry;
import com.app.redcarga.admingeo.domain.repositories.DepartmentCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PgDepartmentCatalogJdbcRepository implements DepartmentCatalogRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<DepartmentEntry> findAllSortedByName() {
        final String sql = """
                SELECT d.department_code, d.department_name
                  FROM geo.departments d
                 ORDER BY d.department_name ASC
                """;
        return jdbc.query(sql, (rs, n) ->
                new DepartmentEntry(
                        DepartmentCode.of(rs.getString("department_code")),
                        rs.getString("department_name").trim()
                )
        );
    }

    @Override
    public boolean exists(DepartmentCode code) {
        final String sql = """
                SELECT EXISTS(
                    SELECT 1
                      FROM geo.departments
                     WHERE department_code = :code
                )
                """;
        Boolean ok = jdbc.queryForObject(sql, Map.of("code", code.getValue()), Boolean.class);
        return Boolean.TRUE.equals(ok);
    }
}
