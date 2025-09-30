package com.app.redcarga.iam.application.internal.acl;

import com.app.redcarga.iam.application.internal.queryservices.RegistrationQueryService;
import com.app.redcarga.iam.domain.model.aggregates.Account;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.repositories.SystemRoleRepository;
import com.app.redcarga.iam.interfaces.acl.IamAccountFacade;
import com.app.redcarga.iam.interfaces.acl.IamAccountSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class IamAccountFacadeImpl implements IamAccountFacade {

    private final RegistrationQueryService registrationQueryService;
    private final SystemRoleRepository systemRoleRepository;

    public IamAccountFacadeImpl(RegistrationQueryService registrationQueryService,
                                SystemRoleRepository systemRoleRepository) {
        this.registrationQueryService = registrationQueryService;
        this.systemRoleRepository = systemRoleRepository;
    }

    @Override
    public Optional<IamAccountSnapshot> findByAccountId(int accountId) {
        // 1) Account (si no existe, devolvemos Optional.empty)
        final Account acc;
        try {
            acc = registrationQueryService.requireAccountById(accountId);
        } catch (IllegalArgumentException notFound) {
            return Optional.empty();
        }

        // 2) Intent abierto (si no hay, usamos "NONE")
        Optional<SignupIntent> optIntent = registrationQueryService.findOpenSignupIntent(acc.getId());
        String state = optIntent.map(si -> si.getStatus().name()).orElse("NONE");

        // 3) systemRoleCode desde catálogo
        String roleCode = systemRoleRepository.findCodeById(acc.getSystemRoleId())
                .orElse("UNKNOWN");

        // 4) emailVerified (si no quieres exponerlo, pon null)
        Boolean emailVerified = acc.isEmailVerified();

        return Optional.of(new IamAccountSnapshot(
                acc.getId(),
                state,
                roleCode,
                emailVerified
        ));
    }

    @Override
    public Optional<IamAccountSnapshot> findByExternalUid(String uid) {
        if (uid == null || uid.isBlank()) return Optional.empty();
        return registrationQueryService.findAccountByExternalUid(uid.trim())
                .map(this::toSnapshot);
    }

    @Override
    public Optional<IamAccountSnapshot> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        String normalized = email.trim().toLowerCase();
        return registrationQueryService.findAccountByEmail(normalized)
                .map(this::toSnapshot);
    }

    // ---------- helper común ----------
    private IamAccountSnapshot toSnapshot(Account acc) {
        Optional<SignupIntent> optIntent = registrationQueryService.findOpenSignupIntent(acc.getId());
        String state = optIntent.map(si -> si.getStatus().name()).orElse("NONE");

        String roleCode = systemRoleRepository.findCodeById(acc.getSystemRoleId())
                .orElse("UNKNOWN");

        Boolean emailVerified = acc.isEmailVerified();

        return new IamAccountSnapshot(
                acc.getId(),
                state,
                roleCode,
                emailVerified
        );
    }
}
