package com.app.redcarga.iam.domain.model.aggregates;

import com.app.redcarga.iam.domain.model.valueobjects.Platform;
import com.app.redcarga.iam.domain.model.valueobjects.SignupStatus;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "iam", name = "signup_intents")
@AttributeOverride(name = "id", column = @Column(name = "signup_intent_id"))
public class SignupIntent extends AuditableAbstractAggregateRoot<SignupIntent> {

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SignupStatus status = SignupStatus.PENDING_EMAIL_VERIFICATION;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_step_at", nullable = false)
    private Instant lastStepAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;

    @Column(name = "verification_sent_count", nullable = false)
    private int verificationSentCount = 0;

    @Column(name = "last_verification_sent_at")
    private Instant lastVerificationSentAt;

    /** Constructor usado por la capa de aplicación: siempre pasar 'now'. */
    public SignupIntent(Integer accountId, Platform platform, Instant expiresAt, Instant now) {
        this.accountId = accountId;
        this.platform = platform;
        this.expiresAt = expiresAt;
        this.lastStepAt = now;
    }

    /** Avanza el estado e imprime 'lastStepAt' con el 'now' recibido. */
    protected void advance(SignupStatus to, Instant now) {
        if (this.status.ordinal() > to.ordinal())
            throw new IllegalStateException("Cannot move backwards");
        this.status = to;
        this.lastStepAt = now;
    }

    public void markEmailVerified(Instant now) {
        if (now.isAfter(expiresAt)) throw new IllegalStateException("Signup intent expired");
        advance(SignupStatus.EMAIL_VERIFIED, now);
    }

    public void markBasicProfileCompleted(Instant now) {
        advance(SignupStatus.BASIC_PROFILE_COMPLETED, now);
    }

    public void complete(Instant now) {
        advance(SignupStatus.DONE, now);
    }

    public boolean canResendVerification(Instant now) {
        return lastVerificationSentAt == null || now.isAfter(lastVerificationSentAt.plusSeconds(60));
    }

    public void registerVerificationSent(Instant now) {
        this.lastVerificationSentAt = now;
        this.verificationSentCount++;
    }
}
