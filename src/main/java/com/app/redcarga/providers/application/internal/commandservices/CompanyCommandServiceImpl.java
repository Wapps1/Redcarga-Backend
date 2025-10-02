package com.app.redcarga.providers.application.internal.commandservices;

import com.app.redcarga.providers.application.internal.outboundservices.acl.IamAccountProviderService;
import com.app.redcarga.providers.application.internal.outboundservices.events.ProvidersEventPublisher;
import com.app.redcarga.providers.application.internal.outboundservices.persistence.CompanyComplianceGateway;
import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterCompanyCommand;
import com.app.redcarga.providers.domain.model.aggregates.Company;
import com.app.redcarga.providers.domain.model.aggregates.CompanyMember;
import com.app.redcarga.providers.domain.model.entities.CompanyMemberRole;
import com.app.redcarga.providers.domain.model.entities.CompanyRole;
import com.app.redcarga.providers.domain.repositories.*;
import com.app.redcarga.providers.domain.services.CompanyCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.shared.events.providers.ProviderOnboardedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private static final String REQUIRED_SIGNUP_STATE = "BASIC_PROFILE_COMPLETED";
    private static final String REQUIRED_ROLE_CODE    = "PROVIDER";
    private static final String ADMIN_ROLE_CODE       = "ADMIN";

    private final CompanyRepository companyRepo;
    private final CompanyMemberRepository memberRepo;
    private final CompanyMemberRoleRepository memberRoleRepo;
    private final CompanyRoleCatalogRepository roleCatalogRepo;

    private final IamAccountProviderService iam;                        // ACL lectura a IAM
    private final ProvidersEventPublisher events;               // after-commit publisher
    private final CompanyComplianceGateway complianceGateway;   // JDBC side-effect
    private final Clock clock = Clock.systemUTC();

    @Override
    public Integer handle(VerifyAndRegisterCompanyCommand c) {
        // 1) Snapshot IAM
        var snap = iam.getByAccountId(c.accountId())
                .orElseThrow(() -> domain("account_not_found")); // 404 en handler

        if (!REQUIRED_ROLE_CODE.equalsIgnoreCase(nullToEmpty(snap.systemRoleCode()))) {
            throw domain("invalid_provider_role");               // 403
        }
        if (!REQUIRED_SIGNUP_STATE.equalsIgnoreCase(nullToEmpty(snap.signupIntentState()))) {
            throw domain("signup_step_not_ready");               // 422
        }

        // 2) Unicidad
        if (companyRepo.existsByCreatedByAccountId(c.accountId()))
            throw domain("company_already_exists_for_account");  // 409
        if (companyRepo.existsByRuc(c.ruc()))
            throw domain("ruc_already_in_use");                  // 409

        // 3) Persistencia: Company
        var company = Company.createSubmitted(
                c.accountId(), c.legalName(), c.tradeName(), c.ruc(), c.email(), c.phone(), c.address()
        );
        company = companyRepo.save(company);

        // 4) Persistencia: Member + ADMIN
        var member = memberRepo.save(CompanyMember.joinAsActive(company, c.accountId()));

        CompanyRole admin = roleCatalogRepo.findByCode(ADMIN_ROLE_CODE)
                .orElseThrow(() -> new IllegalStateException("seed missing: company_roles 'ADMIN'"));

        memberRoleRepo.save(CompanyMemberRole.grant(member, admin));

        // 5) Compliance flag (idempotente)
        complianceGateway.ensurePending(company.getId());

        // 6) Evento after-commit
        events.publish(new ProviderOnboardedEvent(
                UUID.randomUUID(), Instant.now(clock), c.accountId(), 1
        ));

        return company.getId();
    }

    private static DomainException domain(String code) { return new DomainException(code); }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
