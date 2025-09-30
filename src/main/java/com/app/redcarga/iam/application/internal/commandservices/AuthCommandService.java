package com.app.redcarga.iam.application.internal.commandservices;

import com.app.redcarga.iam.application.internal.gateways.AuthProviderGateway;
import com.app.redcarga.iam.domain.model.aggregates.Account;
import com.app.redcarga.iam.domain.model.aggregates.Session;
import com.app.redcarga.iam.domain.model.aggregates.SignupIntent;
import com.app.redcarga.iam.domain.model.commands.CreateSessionCommand;
import com.app.redcarga.iam.domain.model.commands.LogoutCommand;
import com.app.redcarga.iam.domain.model.valueobjects.Platform;
import com.app.redcarga.iam.domain.repositories.AccountRepository;
import com.app.redcarga.iam.domain.repositories.SessionRepository;
import com.app.redcarga.iam.domain.repositories.SignupIntentRepository;
import com.app.redcarga.iam.domain.repositories.SystemRoleRepository;
import com.app.redcarga.iam.infrastructure.jwt.IamJwtIssuer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AuthCommandService {

    private final SessionRepository sessionRepo;
    private final AccountRepository accountRepo;
    private final SignupIntentRepository signupRepo;
    private final AuthProviderGateway authGateway;
    private final Clock clock;
    private final IamJwtIssuer jwtIssuer;
    private final SystemRoleRepository roleRepo;

    public AuthCommandService(SessionRepository sessionRepo,
                              AccountRepository accountRepo,
                              SignupIntentRepository signupRepo,
                              AuthProviderGateway authGateway,
                              Clock clock,
                              IamJwtIssuer jwtIssuer,
                              SystemRoleRepository roleRepo) {
        this.sessionRepo = sessionRepo;
        this.accountRepo = accountRepo;
        this.signupRepo = signupRepo;
        this.authGateway = authGateway;
        this.clock = clock;
        this.jwtIssuer = jwtIssuer;
        this.roleRepo = roleRepo;
    }

    /**
     * Crea una Session y devuelve solo el id (versión existente).
     * La dejo intacta por si la usas en otros lados.
     */
    @Transactional
    public Integer handle(CreateSessionCommand cmd) {
        var platform  = Platform.valueOf(cmd.platform().toUpperCase());
        var expiresAt = Instant.now(clock).plusSeconds(cmd.ttlSeconds());

        InetAddress addr = null;
        var ip = cmd.ipAddress();
        if (ip != null && !ip.isBlank()) {
            try { addr = InetAddress.getByName(ip); }
            catch (UnknownHostException e) { /* si quieres, log.warn y lo dejas null */ }
        }

        var session = new Session(cmd.accountId(), platform, addr, expiresAt);
        sessionRepo.save(session);
        return session.getId();
    }

    /**
     * Nuevo flujo de LOGIN:
     * - Si hay intent abierto -> INCOMPLETE (no crea sesión).
     * - Si no hay intent abierto -> crea sesión + emite JWT IAM.
     */
    @Transactional
    public LoginOutcome loginAndIssueToken(int accountId, String platform, String ip, long ttlSeconds) {
        // 1) Gate de registro: intent "abierto" = PENDING / EMAIL_VERIFIED / BASIC_PROFILE_COMPLETED
        var openIntent = signupRepo.findOpenByAccountId(accountId);
        if (openIntent.isPresent()) {
            var intent = openIntent.get();
            var status = intent.getStatus().name(); // asumiendo enum SignupStatus
            var pending = pendingFrom(status);
            var next = nextStepFrom(status);

            return LoginOutcome.incomplete(
                    accountId,
                    status,
                    pending,
                    next
            );
        }

        // 2) Registro completo (DONE) -> crear Session
        var plat = Platform.valueOf(platform.toUpperCase());
        var expiresAt = Instant.now(clock).plusSeconds(ttlSeconds);
        InetAddress addr = null;
        if (ip != null && !ip.isBlank()) {
            try { addr = InetAddress.getByName(ip); } catch (UnknownHostException ignore) {}
        }

        var session = new Session(accountId, plat, addr, expiresAt);
        sessionRepo.save(session);

        // 3) sys_roles desde Account (ajusta según tu modelo)
        var sysRoles = resolveSystemRoles(accountId);

        // 4) Emitir JWT IAM
        var issued = jwtIssuer.issueAccessToken(accountId, session.getId(), sysRoles);

        return LoginOutcome.ok(
                session.getId(),
                issued.accessToken(),
                issued.tokenType(),
                issued.expiresIn()
        );
    }

    private List<String> resolveSystemRoles(int accountId) {
        return accountRepo.findById(accountId)
                .flatMap(acc -> roleRepo.findCodeById(acc.getSystemRoleId()))
                .map(List::of)
                .orElse(List.of()); // sin rol → lista vacía
    }

    private List<String> pendingFrom(String signupStatus) {
        // Mapea tu enum a "pendientes" legibles para el front
        // Ajusta a tus nombres exactos
        return switch (signupStatus) {
            case "PENDING_EMAIL_VERIFICATION" -> List.of("EMAIL_VERIFICATION");
            case "EMAIL_VERIFIED" -> List.of("BASIC_PROFILE");
            case "BASIC_PROFILE_COMPLETED" -> List.of("REGISTER_COMPANY");
            case "DONE" -> List.of();
            default -> List.of("UNKNOWN");
        };
    }

    private String nextStepFrom(String signupStatus) {
        // Primer pendiente como "next"
        var p = pendingFrom(signupStatus);
        return p.isEmpty() ? "NONE" : p.get(0);
    }

    @Transactional
    public void handle(LogoutCommand cmd) {
        var session = sessionRepo.findById(cmd.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        session.revoke();
        sessionRepo.save(session);

        // opcional: forzar re-login global en Firebase
        accountRepo.findById(session.getAccountId()).ifPresent(acc ->
                authGateway.revokeRefreshTokens(acc.getExternalUid()));
    }

    /** Resultado polimórfico del login */
    public sealed interface LoginOutcome permits LoginOutcome.Ok, LoginOutcome.Incomplete {
        record Ok(Integer sessionId, String accessToken, String tokenType, long expiresIn) implements LoginOutcome {}
        record Incomplete(Integer accountId, String signupStatus, List<String> pending, String nextStep) implements LoginOutcome {}
        static Ok ok(Integer sessionId, String token, String type, long exp) { return new Ok(sessionId, token, type, exp); }
        static Incomplete incomplete(Integer accountId, String status, List<String> pending, String next) { return new Incomplete(accountId, status, pending, next); }
    }
}
