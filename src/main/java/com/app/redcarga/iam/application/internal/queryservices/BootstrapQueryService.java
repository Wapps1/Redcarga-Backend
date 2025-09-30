package com.app.redcarga.iam.application.internal.queryservices;

import com.app.redcarga.iam.application.internal.views.BootstrapView;
import com.app.redcarga.iam.domain.model.aggregates.Account;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.model.queries.GetBootstrapQuery;
import com.app.redcarga.iam.domain.repositories.AccountRepository;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class BootstrapQueryService {

    private final AccountRepository accountRepo;
    private final SignupIntentRepository intentRepo;
    private final Clock clock;

    public BootstrapQueryService(AccountRepository accountRepo,
                                 SignupIntentRepository intentRepo,
                                 Clock clock) {
        this.accountRepo = accountRepo;
        this.intentRepo = intentRepo;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public BootstrapView handle(GetBootstrapQuery q) {
        Account acc = accountRepo.findById(q.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        var me = new BootstrapView.MeView(
                acc.getId(),
                acc.getExternalUid(),
                acc.getEmail(),
                acc.getUsername(),
                resolveRoleCode(acc.getSystemRoleId()), // si necesitas el code, mapea vía catálogo (puedes cachearlo)
                acc.isEmailVerified()
        );

        SignupIntent intent = intentRepo.findOpenByAccountId(acc.getId()).orElse(null);

        var reg = new BootstrapView.RegistrationView(
                /* basicProfile */ "DONE", // hasta que exista Identity
                /* providerOnboarding */ "NOT_STARTED", // hasta que exista Providers
                computeNextStep(acc, intent)
        );

        var features = new BootstrapView.FeaturesView(
                /* mfaEnforced */ false // placeholder para flags
        );

        return new BootstrapView(
                me,
                reg,
                features,
                Instant.now(clock).toString()
        );
    }

    private String computeNextStep(Account acc, SignupIntent intent) {
        if (!acc.isEmailVerified()) return "VERIFY_EMAIL";
        // cuando tengas Identity/Providers, añade aquí los checks y retorna COMPLETE_BASIC_PROFILE/START_PROVIDER_ONBOARDING
        return "NONE";
    }

    private String resolveRoleCode(Integer systemRoleId) {
        // placeholder: si necesitas el code textual para el front,
        // puedes inyectar un pequeño catálogo cacheado o leerlo desde JWT claims.
        return "CLIENT"; // ajusta si vas a resolverlo realmente
    }
}
