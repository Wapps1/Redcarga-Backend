package com.app.redcarga.iam.application.internal.notifications;

import com.app.redcarga.iam.application.internal.gateways.AuthProviderGateway;
import com.app.redcarga.iam.application.internal.queryservices.RegistrationQueryService;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import com.app.redcarga.shared.application.gateways.MailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;

@Service
public class VerificationMailAppService {

    private static final Logger log = LoggerFactory.getLogger(VerificationMailAppService.class);

    private final RegistrationQueryService regQuery;
    private final SignupIntentRepository intentRepo;
    private final AuthProviderGateway authGateway;
    private final MailGateway mailGateway;
    private final Clock clock;

    @Value("${app.iam.verification-continue-url}")
    private String continueUrl;

    @Value("${app.iam.post-verify-redirect:http://localhost:5173/registro/continuar}")
    private String postVerifyRedirect;

    public VerificationMailAppService(RegistrationQueryService regQuery,
                                      SignupIntentRepository intentRepo,
                                      AuthProviderGateway authGateway,
                                      MailGateway mailGateway,
                                      Clock clock) {
        this.regQuery = regQuery;
        this.intentRepo = intentRepo;
        this.authGateway = authGateway;
        this.mailGateway = mailGateway;
        this.clock = clock;
    }

    @Transactional
    public String sendVerificationEmail(Integer accountId) {
        log.info("[EMAIL] Starting verification email for accountId: {}", accountId);
        
        try {
            log.info("[EMAIL] Step 1: Getting account and intent");
            var account = regQuery.requireAccountById(accountId);
            var intent = regQuery.findOpenSignupIntent(accountId)
                    .orElseThrow(() -> new IllegalStateException("No open signup intent"));
            log.info("[EMAIL] Step 1: Account and intent retrieved successfully");

            var now = Instant.now(clock);
            if (!intent.canResendVerification(now)) {
                throw new IllegalStateException("Too many requests. Try later.");
            }

            // Build continueUrl con params
            log.info("[EMAIL] Step 2: Building continue URL");
            String url = continueUrl
                    + (continueUrl.contains("?") ? "&" : "?")
                    + "email=" + URLEncoder.encode(account.getEmail(), StandardCharsets.UTF_8)
                    + "&accountId=" + account.getId()
                    + "&redirect=" + URLEncoder.encode(postVerifyRedirect, StandardCharsets.UTF_8);
            log.info("[EMAIL] Step 2: Continue URL built: {}", url);

            // Link de Firebase
            log.info("[EMAIL] Step 3: Generating Firebase verification link");
            String verificationLink = authGateway.generateEmailVerificationLink(account.getEmail(), url);
            log.info("[EMAIL] Step 3: Firebase link generated successfully");

            // Asunto
            String subject = "Confirma tu correo Red Carga";

            // Cuerpo TEXTO PLANO (siempre)
            String plain = "asdasd";

            // Cuerpo HTML simple (incluye el enlace visible)
            String html = "asd";

            // Enviar (texto + html)
            log.info("[EMAIL] Step 4: Sending email via MailGateway");
            mailGateway.send(account.getEmail(), subject, plain, html);
            log.info("[EMAIL] Step 4: Email sent successfully");

            // Registrar en dominio
            log.info("[EMAIL] Step 5: Registering verification sent in domain");
            intent.registerVerificationSent(now);
            intentRepo.save(intent);
            log.info("[EMAIL] Step 5: Domain updated successfully");

            return verificationLink; // útil en dev/logs
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send verification email for accountId: {}", accountId, e);
            throw e;
        }
    }


}
