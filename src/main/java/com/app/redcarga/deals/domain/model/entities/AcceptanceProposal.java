package com.app.redcarga.deals.domain.model.entities;

import com.app.redcarga.deals.domain.model.valueobjects.AcceptanceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Aggregate: AcceptanceProposal mapped to deals.acceptance_proposal
 * Implements factories and domain methods with invariants.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "deals", name = "acceptance_proposal",
       uniqueConstraints = @UniqueConstraint(name = "uq_acceptance_idempotency", columnNames = {"idempotency_key"}))
public class AcceptanceProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acceptance_id")
    private Integer acceptanceId;

    @Column(name = "quote_id", nullable = false)
    private Integer quoteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_code", nullable = false, length = 16)
    private AcceptanceStatus status;

    @Column(name = "initiator_user_id", nullable = false)
    private Integer initiatorUserId;

    @Column(name = "resolver_user_id")
    private Integer resolverUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "idempotency_key", length = 64, unique = true)
    private String idempotencyKey;

    /** Factory to propose an acceptance. Validates invariants. */
    public static AcceptanceProposal propose(Integer quoteId, Integer initiatorUserId, String idempotencyKey) {
        if (quoteId == null || quoteId <= 0) throw new IllegalArgumentException("quote_required");
        if (initiatorUserId == null || initiatorUserId <= 0) throw new IllegalArgumentException("initiator_required");
        if (idempotencyKey != null && idempotencyKey.length() > 64) throw new IllegalArgumentException("idempotency_key_invalid");

        AcceptanceProposal p = new AcceptanceProposal();
        p.quoteId = quoteId;
        p.initiatorUserId = initiatorUserId;
        p.status = AcceptanceStatus.PENDIENTE;
        p.createdAt = Instant.now();
        p.idempotencyKey = (idempotencyKey == null || idempotencyKey.isBlank()) ? null : idempotencyKey.trim();
        return p;
    }

    /** Confirm the proposal. Throws if already resolved or invalid resolver. */
    public void confirm(Integer resolverUserId) {
        requirePositive(resolverUserId, "resolver_user_id_invalid");
        if (this.initiatorUserId.equals(resolverUserId)) throw new IllegalStateException("cannot_resolve_own_proposal");
        if (this.status != AcceptanceStatus.PENDIENTE) throw new IllegalStateException("acceptance_already_resolved");
        this.status = AcceptanceStatus.CONFIRMADA;
        this.resolverUserId = resolverUserId;
        this.resolvedAt = Instant.now();
    }

    /** Reject the proposal. Throws if already resolved or invalid resolver. */
    public void reject(Integer resolverUserId) {
        requirePositive(resolverUserId, "resolver_user_id_invalid");
        if (this.initiatorUserId.equals(resolverUserId)) throw new IllegalStateException("cannot_resolve_own_proposal");
        if (this.status != AcceptanceStatus.PENDIENTE) throw new IllegalStateException("acceptance_already_resolved");
        this.status = AcceptanceStatus.RECHAZADA;
        this.resolverUserId = resolverUserId;
        this.resolvedAt = Instant.now();
    }

    private static void requirePositive(Integer v, String key) {
        if (v == null || v <= 0) throw new IllegalArgumentException(key);
    }

    // package-private setter for infrastructure mappers if required
    void setAcceptanceId(Integer acceptanceId) { this.acceptanceId = acceptanceId; }
}
