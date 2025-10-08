package com.app.redcarga.providers.infrastructure.persistence.jdbc;

import com.app.redcarga.providers.application.internal.views.CompanyView;
import com.app.redcarga.providers.domain.queries.CompanyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PgCompanyQueryRepository implements CompanyQueryRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<CompanyView> findById(int companyId) {
        var sql = """
            SELECT c.company_id,
                   c.legal_name,
                   c.trade_name,
                   c.ruc,
                   c.email,
                   c.phone,
                   c.address,
                   c.status,
                   COALESCE(cc.docs_status, 'PENDING') AS docs_status,
                   c.created_by_account_id,
                   (SELECT COUNT(*) FROM providers.company_members m
                      WHERE m.company_id = c.company_id AND m.status = 'ACTIVE') AS members_count
            FROM providers.companies c
            LEFT JOIN providers.company_compliance cc ON cc.company_id = c.company_id
            WHERE c.company_id = :companyId
        """;
        var params = new MapSqlParameterSource().addValue("companyId", companyId);

        return jdbc.query(sql, params, rs -> {
            if (!rs.next()) return Optional.empty();
            var v = new CompanyView(
                    rs.getInt("company_id"),
                    rs.getString("legal_name"),
                    rs.getString("trade_name"),
                    rs.getString("ruc"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("status"),
                    rs.getString("docs_status"),
                    rs.getInt("created_by_account_id"),
                    rs.getInt("members_count")
            );
            return Optional.of(v);
        });
    }

    @Override
    public boolean hasActiveMembership(int companyId, int accountId) {
        var sql = """
            SELECT 1
            FROM providers.company_members
            WHERE company_id = :companyId
              AND account_id = :accountId
              AND status = 'ACTIVE'
            LIMIT 1
        """;
        var params = new MapSqlParameterSource()
                .addValue("companyId", companyId)
                .addValue("accountId", accountId);
        return Boolean.TRUE.equals(jdbc.query(sql, params, rs -> rs.next() ? Boolean.TRUE : Boolean.FALSE));
    }
}
