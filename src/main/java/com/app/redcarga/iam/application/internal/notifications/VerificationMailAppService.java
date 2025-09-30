package com.app.redcarga.iam.application.internal.notifications;

import com.app.redcarga.iam.application.internal.gateways.AuthProviderGateway;
import com.app.redcarga.iam.application.internal.queryservices.RegistrationQueryService;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import com.app.redcarga.shared.application.gateways.MailGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;

@Service
public class VerificationMailAppService {

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
        var account = regQuery.requireAccountById(accountId);
        var intent = regQuery.findOpenSignupIntent(accountId)
                .orElseThrow(() -> new IllegalStateException("No open signup intent"));

        var now = Instant.now(clock);
        if (!intent.canResendVerification(now)) {
            throw new IllegalStateException("Too many requests. Try later.");
        }

        // Build continueUrl con params
        String url = continueUrl
                + (continueUrl.contains("?") ? "&" : "?")
                + "email=" + URLEncoder.encode(account.getEmail(), StandardCharsets.UTF_8)
                + "&accountId=" + account.getId()
                + "&redirect=" + URLEncoder.encode(postVerifyRedirect, StandardCharsets.UTF_8);

        // Link de Firebase
        String verificationLink = authGateway.generateEmailVerificationLink(account.getEmail(), url);

        // Asunto
        String subject = "Confirma tu correo Red Carga";

        // Cuerpo TEXTO PLANO (siempre)
        String plain = "asdasd";

        // Cuerpo HTML simple (incluye el enlace visible)
        String html = "asd";

        // Enviar (texto + html)
        mailGateway.send(account.getEmail(), subject, plain, html);

        // Registrar en dominio
        intent.registerVerificationSent(now);
        intentRepo.save(intent);

        return verificationLink; // útil en dev/logs
    }


}
