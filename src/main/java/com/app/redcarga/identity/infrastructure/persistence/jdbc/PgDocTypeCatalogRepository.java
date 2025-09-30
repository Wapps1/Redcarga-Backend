package com.app.redcarga.identity.infrastructure.persistence.jdbc;

import com.app.redcarga.identity.domain.repositories.DocTypeCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgDocTypeCatalogRepository implements DocTypeCatalogRepository {

    private final JdbcTemplate jdbc;

    @Override
    public Optional<Integer> findIdByCode(String code) {
        try {
            Integer id = jdbc.queryForObject(
                    "select doc_type_id from identity.doc_types where code = ?",
                    Integer.class, code
            );
            return Optional.ofNullable(id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> findCodeById(Integer id) {
        try {
            String code = jdbc.queryForObject(
                    "select code from identity.doc_types where doc_type_id = ?",
                    String.class, id
            );
            return Optional.ofNullable(code);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsCode(String code) {
        Boolean exists = jdbc.queryForObject(
                "select exists(select 1 from identity.doc_types where code = ?)",
                Boolean.class, code
        );
        return exists != null && exists;
    }
}
