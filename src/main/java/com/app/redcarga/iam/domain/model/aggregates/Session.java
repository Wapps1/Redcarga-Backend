package com.app.redcarga.iam.domain.model.aggregates;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.AbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.time.Instant;

import com.app.redcarga.iam.domain.model.valueobjects.Platform;
import com.app.redcarga.iam.domain.model.valueobjects.SessionStatus;
import com.app.redcarga.iam.domain.model.events.SessionCreated;
import com.app.redcarga.iam.domain.model.events.SessionRevoked;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "iam", name = "sessions")
public class Session extends AbstractAggregateRoot<Session> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @JdbcTypeCode(SqlTypes.INET)
    @Column(name = "ip_address", columnDefinition = "inet")
    private InetAddress ipAddress;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Session(Integer accountId, Platform platform, InetAddress ipAddress, Instant expiresAt) {
        this.accountId = accountId;
        this.platform = platform;
        this.ipAddress = ipAddress;
        this.expiresAt = expiresAt;
    }

    /** Si no seteaste createdAt desde el servicio, se pone ahora. */
    @PrePersist
    void prePersist() {
        if (this.createdAt == null) this.createdAt = Instant.now();
    }

    /** Emite el evento con el id ya asignado */
    @PostPersist
    void postPersist() {
        registerEvent(new SessionCreated(this.id, this.accountId));
    }

    public void touch(Instant now) {
        if (status == SessionStatus.ACTIVE) this.lastSeenAt = now;
    }

    public void revoke() {
        if (status != SessionStatus.REVOKED) {
            this.status = SessionStatus.REVOKED;
            registerEvent(new SessionRevoked(this.id, this.accountId));
        }
    }

    public void expire(Instant now) {
        if (now.isAfter(expiresAt)) this.status = SessionStatus.EXPIRED;
    }
}
