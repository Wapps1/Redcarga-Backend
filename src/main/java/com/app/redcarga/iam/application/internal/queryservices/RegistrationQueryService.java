package com.app.redcarga.iam.application.internal.queryservices;

import com.app.redcarga.iam.domain.model.aggregates.Account;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.repositories.AccountRepository;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RegistrationQueryService {

    private final AccountRepository accountRepo;
    private final SignupIntentRepository intentRepo;

    public RegistrationQueryService(AccountRepository accountRepo,
                                    SignupIntentRepository intentRepo) {
        this.accountRepo = accountRepo;
        this.intentRepo = intentRepo;
    }

    /** Lanza si no existe. Úsalo cuando la ausencia sea error de flujo. */
    public Account requireAccountById(Integer accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /** Devuelve true si el email pertenece a esa cuenta. */
    public boolean emailBelongsToAccount(Integer accountId, String email) {
        return accountRepo.findById(accountId)
                .map(a -> a.getEmail().equalsIgnoreCase(email))
                .orElse(false);
    }

    /** Intent abierto (PENDING_EMAIL_VERIFICATION / EMAIL_VERIFIED / BASIC_PROFILE_COMPLETED). */
    public Optional<SignupIntent> findOpenSignupIntent(Integer accountId) {
        return intentRepo.findOpenByAccountId(accountId);
    }

    /** Búsqueda auxiliar por email, por si la necesitas en otros flujos. */
    public Optional<Account> findAccountByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return accountRepo.findByEmailIgnoreCase(email.trim());
    }

    public Optional<Account> findAccountByExternalUid(String externalUid) {
        if (externalUid == null || externalUid.isBlank()) return Optional.empty();
        return accountRepo.findByExternalUid(externalUid.trim());
    }
}
