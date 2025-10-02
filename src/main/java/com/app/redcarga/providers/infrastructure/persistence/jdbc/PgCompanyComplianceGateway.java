package com.app.redcarga.providers.infrastructure.persistence.jdbc;

import com.app.redcarga.providers.application.internal.outboundservices.persistence.CompanyComplianceGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PgCompanyComplianceGateway implements CompanyComplianceGateway {
    private final JdbcTemplate jdbc;

    @Override
    public void ensurePending(Integer companyId) {
        jdbc.update("""
            INSERT INTO providers.company_compliance (company_id, docs_status)
            VALUES (?, 'PENDING')
            ON CONFLICT (company_id) DO NOTHING
        """, companyId);
    }
}
