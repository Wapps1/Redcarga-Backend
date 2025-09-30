package com.app.redcarga.iam.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.iam.domain.repositories.SystemRoleRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;

@Repository
public class JdbcSystemRoleRepository implements SystemRoleRepository {

    private final JdbcTemplate jdbc;

    public JdbcSystemRoleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Integer> findIdByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        try {
            Integer id = jdbc.queryForObject(
                    "SELECT system_role_id FROM iam.system_roles WHERE UPPER(code) = ? LIMIT 1",
                    Integer.class,
                    code.toUpperCase(Locale.ROOT)
            );
            return Optional.ofNullable(id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> findCodeById(Integer id) {
        if (id == null || id <= 0) return Optional.empty();
        try {
            String code = jdbc.queryForObject(
                    "SELECT code FROM iam.system_roles WHERE system_role_id = ?",
                    String.class,
                    id
            );
            return Optional.ofNullable(code);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByCode(String code) {
        if (code == null || code.isBlank()) return false;
        Boolean exists = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM iam.system_roles WHERE UPPER(code) = ?)",
                Boolean.class,
                code.toUpperCase(Locale.ROOT)
        );
        return Boolean.TRUE.equals(exists);
    }
}
