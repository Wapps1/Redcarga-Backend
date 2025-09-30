package com.app.redcarga.iam.interfaces.rest;

import com.app.redcarga.iam.application.internal.commandservices.AuthCommandService;
import com.app.redcarga.iam.application.internal.commandservices.RegistrationCommandService;
import com.app.redcarga.iam.application.internal.gateways.AuthProviderGateway;
import com.app.redcarga.iam.application.internal.notifications.VerificationMailAppService;
import com.app.redcarga.iam.application.internal.queryservices.AccountIdentityQueryService;
import com.app.redcarga.iam.application.internal.queryservices.BootstrapQueryService;
import com.app.redcarga.iam.application.internal.queryservices.RegistrationQueryService;
import com.app.redcarga.iam.application.internal.views.BootstrapView;
import com.app.redcarga.iam.application.internal.views.RegisterStartResult;
import com.app.redcarga.iam.domain.model.commands.CreateSessionCommand;
import com.app.redcarga.iam.domain.model.commands.LogoutCommand;
import com.app.redcarga.iam.domain.model.commands.MarkEmailVerifiedCommand;
import com.app.redcarga.iam.domain.model.commands.RegisterStartCommand;
import com.app.redcarga.iam.domain.model.queries.GetBootstrapQuery;
import com.app.redcarga.iam.interfaces.rest.requests.LoginRequest;
import com.app.redcarga.iam.interfaces.rest.requests.LogoutRequest;
import com.app.redcarga.iam.interfaces.rest.requests.RegisterStartRequest;
import com.app.redcarga.iam.interfaces.rest.responses.ErrorResponse;
import com.app.redcarga.iam.interfaces.rest.responses.LoginIncompleteResponse;
import com.app.redcarga.iam.interfaces.rest.responses.LoginOkResponse;
import com.app.redcarga.iam.interfaces.security.CurrentAccountResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/iam")
public class IamController {

    private final RegistrationCommandService registrationService;
    private final AuthCommandService authService;
    private final BootstrapQueryService bootstrapService;
    private final CurrentAccountResolver currentAccount;
    private final RegistrationQueryService registrationQueryService;
    private final AuthProviderGateway authProviderGateway;
    private final VerificationMailAppService verificationMailAppService;
    private final AccountIdentityQueryService accountIdentityQueryService;

    public IamController(RegistrationCommandService registrationService,
                         AuthCommandService authService,
                         BootstrapQueryService bootstrapService,
                         CurrentAccountResolver currentAccount,
                         AuthProviderGateway authProviderGateway,
                         RegistrationQueryService registrationQueryService,
                         VerificationMailAppService verificationMailAppService,
                         AccountIdentityQueryService accountIdentityQueryService) { // NUEVO
        this.registrationService = registrationService;
        this.authService = authService;
        this.bootstrapService = bootstrapService;
        this.currentAccount = currentAccount;
        this.authProviderGateway = authProviderGateway;
        this.registrationQueryService = registrationQueryService; // NUEVO
        this.verificationMailAppService = verificationMailAppService;
        this.accountIdentityQueryService = accountIdentityQueryService;
    }

    // ============ Públicos ============

    @PostMapping("/register-start")
    public ResponseEntity<RegisterStartResult> registerStart(@RequestBody RegisterStartRequest req) {
        // Convertimos password -> char[] y la blanqueamos después
        char[] rawPassword = req.password() != null ? req.password().toCharArray() : new char[0];
        try {
            var cmd = new RegisterStartCommand(
                    req.email(),
                    req.username(),
                    rawPassword,              // char[]
                    req.roleCode(),
                    req.platform(),
                    req.idempotencyKey()
            );
            RegisterStartResult result = registrationService.handle(cmd);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } finally {
            Arrays.fill(rawPassword, '\0'); // limpiar memoria
        }
    }

    // ============ Protegidos ============

    @SecurityRequirement(name = "firebase")
    @PostMapping("/resend-email-verification")
    public ResponseEntity<?> resendEmailVerification() {
        Integer accountId = currentAccount.requireAccountId();
        String link = verificationMailAppService.sendVerificationEmail(accountId); // <— aquí
        return ResponseEntity.accepted().body(Map.of(
                "message", "Verification email sent",
                "verificationLink", link // lo devuelves en dev; quítalo en prod
        ));
    }


    @SecurityRequirement(name = "firebase") // Solo para Swagger UI
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req,
                                   org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken principal) {
        // 1) Resolver accountId sin tocar repos aquí
        var accountIdOpt = accountIdentityQueryService.resolveAccountId(principal);
        if (accountIdOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ErrorResponse.of("account_not_found_or_invalid_token"));
        }
        Integer accountId = accountIdOpt.get();

        // 2) Gate + emitir token si corresponde
        long ttl = req.ttlSeconds() != null ? req.ttlSeconds() : 7200L; // 2h en dev
        var outcome = authService.loginAndIssueToken(accountId, req.platform(), req.ip(), ttl);

        if (outcome instanceof AuthCommandService.LoginOutcome.Incomplete inc) {
            return ResponseEntity.status(423).body(
                    LoginIncompleteResponse.of(inc.signupStatus(), inc.accountId(), inc.pending(), inc.nextStep())
            );
        }
        var ok = (AuthCommandService.LoginOutcome.Ok) outcome;
        return ResponseEntity.ok(LoginOkResponse.of(ok.sessionId(), ok.accessToken(), ok.expiresIn()));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest req) {
        // Por ahora pedimos sessionId explícito (puedes mejorar buscando la sesión activa por platform)
        authService.handle(new LogoutCommand(req.sessionId()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bootstrap")
    public ResponseEntity<BootstrapView> bootstrap() {
        Integer accountId = currentAccount.requireAccountId();
        var view = bootstrapService.handle(new GetBootstrapQuery(accountId));
        return ResponseEntity.ok(view);
    }

    @GetMapping("/email-verification/continue")
    public ResponseEntity<?> emailVerificationContinue(
            @RequestParam("email") String email,
            @RequestParam("accountId") Integer accountId,
            @RequestParam(value = "redirect", required = false) String redirect
    ) {
        if (!registrationQueryService.emailBelongsToAccount(accountId, email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "email/accountId mismatch"));
        }

        boolean verified = authProviderGateway.isEmailVerified(email);
        if (verified) {
            registrationService.handle(new MarkEmailVerifiedCommand(accountId));
        }

        // Redirección opcional
        if (redirect != null && !redirect.isBlank()) {
            // whitelist simple (ajústala a tus dominios)
            boolean allowed =
                    redirect.startsWith("http://localhost:5173") ||
                            redirect.startsWith("https://app.redcarga.com");

            if (!allowed) {
                return ResponseEntity.badRequest().body(Map.of("error", "invalid redirect"));
            }

            String target = redirect + (redirect.contains("?") ? "&" : "?") + "verified=" + verified;
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", target)
                    .build();
        }

        return ResponseEntity.noContent().build();
    }


}
