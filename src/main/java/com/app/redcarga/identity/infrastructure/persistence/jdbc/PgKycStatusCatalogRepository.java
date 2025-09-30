package com.app.redcarga.identity.infrastructure.persistence.jdbc;

import com.app.redcarga.identity.domain.repositories.KycStatusCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgKycStatusCatalogRepository implements KycStatusCatalogRepository {

    private final JdbcTemplate jdbc;

    @Override
    public Optional<Integer> findIdByCode(String code) {
        try {
            Integer id = jdbc.queryForObject(
                    "select kyc_status_id from identity.kyc_status where code = ?",
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
                    "select code from identity.kyc_status where kyc_status_id = ?",
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
                "select exists(select 1 from identity.kyc_status where code = ?)",
                Boolean.class, code
        );
        return exists != null && exists;
    }
}
