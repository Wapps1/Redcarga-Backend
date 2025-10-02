package com.app.redcarga.iam.application.internal.commandservices;

import com.app.redcarga.iam.application.internal.gateways.AuthProviderGateway;
import com.app.redcarga.iam.application.internal.notifications.VerificationMailAppService;
import com.app.redcarga.iam.application.internal.views.RegisterStartResult;
import com.app.redcarga.iam.domain.model.aggregates.Account;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.model.commands.RegisterStartCommand;
import com.app.redcarga.iam.domain.model.commands.MarkEmailVerifiedCommand;
import com.app.redcarga.iam.domain.model.valueobjects.Platform;
import com.app.redcarga.iam.domain.model.valueobjects.SignupStatus;
import com.app.redcarga.iam.domain.repositories.AccountRepository;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import com.app.redcarga.iam.domain.repositories.SystemRoleRepository;
import com.app.redcarga.shared.domain.model.valueobjects.Email;
import com.app.redcarga.iam.domain.model.valueobjects.ExternalUid;
import com.app.redcarga.iam.domain.model.valueobjects.Username;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;

@Service
public class RegistrationCommandService {

    private final AccountRepository accountRepo;
    private final SignupIntentRepository intentRepo;
    private final SystemRoleRepository roleRepo;
    private final AuthProviderGateway authGateway;
    private final Clock clock;
    private final VerificationMailAppService verificationMailAppService;

    @Value("${app.iam.verification-continue-url}")
    private String continueUrl;
    @Value("${app.iam.signup-intent-ttl-seconds:86400}")
    private long intentTtlSeconds;
    @Value("${app.iam.verification-resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Value("${app.iam.post-verify-redirect:http://localhost:5173/registro/continuar}")
    private String postVerifyRedirect;

    public RegistrationCommandService(AccountRepository accountRepo,
                                      SignupIntentRepository intentRepo,
                                      SystemRoleRepository roleRepo,
                                      AuthProviderGateway authGateway,
                                      Clock clock,
                                      VerificationMailAppService verificationMailAppService) {
        this.accountRepo = accountRepo;
        this.intentRepo = intentRepo;
        this.roleRepo = roleRepo;
        this.authGateway = authGateway;
        this.clock = clock;
        this.verificationMailAppService = verificationMailAppService;
    }

    @Transactional
    public RegisterStartResult handle(RegisterStartCommand cmd) {
        // 1) Validaciones de unicidad (antes de tocar Firebase)
        if (accountRepo.existsByEmail(cmd.email()))
            throw new IllegalArgumentException("Email already in use");
        if (accountRepo.existsByUsername(cmd.username()))
            throw new IllegalArgumentException("Username already in use");

        // 2) Crear usuario en Firebase
        String uid = authGateway.createUser(cmd.email(), cmd.rawPassword());
        // Limpia la contraseña del record si la guardaste como char[] en el command
        // Arrays.fill(cmd.rawPassword(), '\0'); // si la tienes como char[]

        // 3) Resolver system_role_id por code
        Integer systemRoleId = roleRepo.findIdByCode(cmd.roleCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown role code: " + cmd.roleCode()));

        // 4) Crear Account
        var account = new Account(new ExternalUid(uid), new Email(cmd.email()), new Username(cmd.username()), systemRoleId);
        accountRepo.save(account);

        // 5) Claims básicos en Firebase (roles + username)
        authGateway.setCustomClaims(uid, Map.of(
                "sys_roles", new String[]{cmd.roleCode().toUpperCase()},
                "username", cmd.username()
        ));

        // 6) Crear/obtener SignupIntent abierto
        var platform = Platform.valueOf(cmd.platform().toUpperCase());
        var now = Instant.now(clock);
        var expiresAt = now.plusSeconds(intentTtlSeconds);

        var intentOpt = intentRepo.findOpenByAccountId(account.getId());
        var intent = intentOpt.orElseGet(() -> {
            var si = new SignupIntent(account.getId(), platform, expiresAt, now);
            return intentRepo.save(si);
        });

        String verifyLink = verificationMailAppService.sendVerificationEmail(account.getId());

        return new RegisterStartResult(
                account.getId(), intent.getId(), account.getEmail(), account.isEmailVerified(), verifyLink
        );
        }

    @Transactional
    public void handle(MarkEmailVerifiedCommand cmd) {
        // Marca verificado y avanza intent si existe
        var acc = accountRepo.findById(cmd.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        acc.markEmailVerified();
        accountRepo.save(acc);

        var now = Instant.now(clock);

        intentRepo.findOpenByAccountId(acc.getId()).ifPresent(intent -> {
            if (intent.getStatus() == SignupStatus.PENDING_EMAIL_VERIFICATION) {
                intent.markEmailVerified(now);
                intentRepo.save(intent);
            }
        });
    }
}
