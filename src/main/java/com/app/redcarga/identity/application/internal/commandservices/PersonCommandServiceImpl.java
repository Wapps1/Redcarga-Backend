package com.app.redcarga.identity.application.internal.commandservices;

import com.app.redcarga.identity.application.internal.outboundservices.acl.IamAccountService;

import com.app.redcarga.identity.application.internal.outboundservices.events.IdentityEventPublisher;
import com.app.redcarga.identity.domain.model.aggregates.Person;
import com.app.redcarga.identity.domain.model.commands.VerifyAndCreatePersonCommand;
import com.app.redcarga.identity.domain.model.entities.KycVerification;
import com.app.redcarga.identity.domain.model.entities.VerificationAttempt;
import com.app.redcarga.identity.domain.model.valueobjects.BirthDate;
import com.app.redcarga.identity.domain.model.valueobjects.DocNumber;
import com.app.redcarga.identity.domain.model.valueobjects.DocTypeCode;
import com.app.redcarga.identity.domain.model.valueobjects.FullName;
import com.app.redcarga.identity.domain.repositories.DocTypeCatalogRepository;
import com.app.redcarga.identity.domain.repositories.KycStatusCatalogRepository;
import com.app.redcarga.identity.domain.repositories.KycVerificationRepository;
import com.app.redcarga.identity.domain.repositories.PersonRepository;
import com.app.redcarga.identity.domain.repositories.VerificationAttemptRepository;
import com.app.redcarga.identity.domain.services.PersonCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.shared.events.identity.ClientKycPassedEvent;
import com.app.redcarga.shared.events.identity.ProviderKycPassedEvent;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonCommandServiceImpl implements PersonCommandService {

    private static final String STATUS_EMAIL_VERIFIED = "EMAIL_VERIFIED";
    private static final String STATUS_KYC_PASSED_CODE = "PASSED";
    private static final String ROLE_PROVIDER = "PROVIDER";
    private static final String PROVIDER_CODE_LOCAL = "LOCAL";

    private final IamAccountService iam;
    private final PersonRepository personRepo;
    private final KycVerificationRepository kycRepo;
    private final VerificationAttemptRepository attemptRepo;
    private final DocTypeCatalogRepository docTypeCatalog;
    private final KycStatusCatalogRepository kycStatusCatalog;
    private final IdentityEventPublisher events;

    @Override
    public Person handle(VerifyAndCreatePersonCommand c) {
        // 1) Snapshot IAM
        var snap = iam.getByAccountId(c.accountId())
                .orElseThrow(() -> notFound("account_not_found"));

        if (!STATUS_EMAIL_VERIFIED.equals(snap.signupIntentState())) {
            throw conflict("signup_intent_invalid_state");
        }

        // 2) Validaciones
        var docTypeId = docTypeCatalog.findIdByCode(c.docTypeCode())
                .orElseThrow(() -> invalid("invalid_doc_type"));

        final DocTypeCode typeEnum;
        try {
            typeEnum = DocTypeCode.valueOf(c.docTypeCode());
        } catch (IllegalArgumentException ex) {
            throw invalid("invalid_doc_type");
        }

        var normalizedDocNumber = DocNumber.of(typeEnum, c.docNumber()).toString();

        var birth = new BirthDate(c.birthDate());
        if (!birth.isAdult(18)) {
            throw new com.app.redcarga.identity.domain.exceptions.UnderagePersonException();
        }

        if (personRepo.existsByDocTypeIdAndDocNumber(docTypeId, normalizedDocNumber)) {
            throw conflict("document_already_exists");
        }

        // 3) Persistir Person
        var person = new Person(
                c.accountId(),
                new FullName(c.fullName()),
                birth,
                docTypeId,
                normalizedDocNumber,
                c.phone(),
                c.ruc()
        );
        person = personRepo.save(person);

        // 4) KYC = PASSED
        int passedId = kycStatusCatalog.findIdByCode(STATUS_KYC_PASSED_CODE)
                .orElseThrow(() -> new IllegalStateException("seed missing: kyc_status 'PASSED'"));

        var verification = new KycVerification(
                person,
                PROVIDER_CODE_LOCAL,
                UUID.randomUUID().toString(), // session_id
                passedId
        );
        verification.markPassed(new Date());
        kycRepo.save(verification);

        // 5) Auditoría intento
        var attempt = new VerificationAttempt(
                verification,
                PROVIDER_CODE_LOCAL,
                null,
                "OK",
                null,
                new Date()
        );
        attemptRepo.save(attempt);

        // 6) Evento tipado (sin PII), AFTER_COMMIT gracias a @Transactional en este service
        var eventId = UUID.randomUUID().toString();
        var now = Instant.now();
        String role = Optional.ofNullable(snap.systemRoleCode()).orElse("CLIENT");

        if (ROLE_PROVIDER.equalsIgnoreCase(role)) {
            events.publish(new ProviderKycPassedEvent(eventId, now, person.getAccountId(), 1));
        } else {
            events.publish(new ClientKycPassedEvent(eventId, now, person.getAccountId(), 1));
        }

        return person;
    }

    // Helpers para mapear a HTTP en ControllerAdvice
    private DomainException notFound(String code) { return new DomainException(code); }
    private DomainException conflict(String code) { return new DomainException(code); }
    private DomainException invalid(String code)  { return new DomainException(code); }
}
