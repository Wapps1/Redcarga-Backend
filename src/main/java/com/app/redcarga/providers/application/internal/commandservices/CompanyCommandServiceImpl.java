package com.app.redcarga.providers.application.internal.commandservices;

import com.app.redcarga.providers.application.internal.outboundservices.acl.IamAccountProviderService;
import com.app.redcarga.providers.application.internal.outboundservices.events.ProvidersEventPublisher;
import com.app.redcarga.providers.application.internal.outboundservices.persistence.CompanyComplianceGateway;
import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterCompanyCommand;
import com.app.redcarga.providers.domain.model.commands.VerifyAndRegisterOperatorCommand;
import com.app.redcarga.providers.domain.model.aggregates.Company;
import com.app.redcarga.providers.domain.model.aggregates.CompanyMember;
import com.app.redcarga.providers.domain.model.entities.CompanyMemberRole;
import com.app.redcarga.providers.domain.model.entities.CompanyRole;
import com.app.redcarga.providers.domain.repositories.*;
import com.app.redcarga.providers.domain.services.CompanyCommandService;
import com.app.redcarga.providers.domain.services.CompanyMembershipQueryService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.shared.events.providers.ProviderOnboardedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private static final Logger log = LoggerFactory.getLogger(CompanyCommandServiceImpl.class);

    private static final String REQUIRED_SIGNUP_STATE = "BASIC_PROFILE_COMPLETED";
    private static final String REGISTER_SIGNUP_STATE = "NONE";
    private static final String REQUIRED_ROLE_CODE    = "PROVIDER";
    private static final Integer ADMIN_ROLE_CODE       = 1;

    private final CompanyRepository companyRepo;
    private final CompanyMemberRepository memberRepo;
    private final CompanyMemberRoleRepository memberRoleRepo;
    private final CompanyRoleCatalogRepository roleCatalogRepo;

    private final IamAccountProviderService iam;                        // ACL lectura a IAM
    private final ProvidersEventPublisher events;               // after-commit publisher
    private final CompanyComplianceGateway complianceGateway;   // JDBC side-effect
    private final CompanyMembershipQueryService membershipQuery;
    private final Clock clock = Clock.systemUTC();

    @Override
    public Integer handle(VerifyAndRegisterCompanyCommand c) {
        // 1) Snapshot IAM
        var snap = iam.getByAccountId(c.accountId())
                .orElseThrow(() -> domain("account_not_found")); // 404 en handler

        log.debug("adminSnap for account {} => {}", c.accountId(), snap);

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

        CompanyRole admin = roleCatalogRepo.findById(ADMIN_ROLE_CODE)
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

    @Override
    public String registerOperator(VerifyAndRegisterOperatorCommand c) {
        // 1) Snapshot IAM: admin
        var adminSnap = iam.getByAccountId(c.adminId())
                .orElseThrow(() -> domain("admin_account_not_found"));

        //log.debug("adminSnap for account {} => {}", c.adminId(), adminSnap);

        if (!REQUIRED_ROLE_CODE.equalsIgnoreCase(nullToEmpty(adminSnap.systemRoleCode()))) {
            throw domain("invalid_provider_role");
        }
        if (!REGISTER_SIGNUP_STATE.equalsIgnoreCase(nullToEmpty(adminSnap.signupIntentState()))) {
            throw domain(String.format(
                    "signup_step_not_ready_from_provider (adminId=%s, state=%s)",
                    c.adminId(),
                    nullToEmpty(adminSnap.signupIntentState())
            ));
        }

        // 1b) Snapshot IAM: operator
        var operatorSnap = iam.getByAccountId(c.operatorId())
                .orElseThrow(() -> domain("operator_account_not_found"));
        if (!REQUIRED_ROLE_CODE.equalsIgnoreCase(nullToEmpty(operatorSnap.systemRoleCode()))) {
            throw domain("invalid_provider_role");
        }
        if (!REQUIRED_SIGNUP_STATE.equalsIgnoreCase(nullToEmpty(operatorSnap.signupIntentState()))) {
            throw domain("signup_step_not_ready_from_operator");
        }

        // 2) Verificar que admin tiene rol ADMIN en la company
        if (!membershipQuery.hasAnyRole(c.companyId(), c.adminId(), List.of(ADMIN_ROLE_CODE))) {
            throw domain("admin_not_authorized");
        }

        // 3) Cargar company
        var company = companyRepo.findById(c.companyId())
                .orElseThrow(() -> domain("company_not_found"));

        // 4) Persistencia: Member + ROLE (ej. DRIVER)
        var member = memberRepo.save(CompanyMember.joinAsActive(company, c.operatorId()));

        CompanyRole role = roleCatalogRepo.findById(c.roleId())
                .orElseThrow(() -> new IllegalStateException("seed missing: company_roles 'DRIVER'"));

        memberRoleRepo.save(CompanyMemberRole.grant(member, role));

        events.publish(new ProviderOnboardedEvent(
                UUID.randomUUID(), Instant.now(clock), c.operatorId(), 1
        ));

        return "ok";
    }

    private static DomainException domain(String code) { return new DomainException(code); }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
