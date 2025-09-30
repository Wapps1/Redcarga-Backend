package com.app.redcarga.identity.domain.model.entities;

import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.identity.domain.model.aggregates.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "identity", name = "kyc_verifications",
        indexes = {
                @Index(name = "ix_kyc_ver_person", columnList = "person_id"),
                @Index(name = "ix_kyc_ver_status", columnList = "kyc_status_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_kyc_session", columnNames = {"session_id"})
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "verification_id")),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", nullable = false))
})
public class KycVerification extends AuditableAbstractAggregateRoot<KycVerification> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_kyc_ver_person"))
    private Person person;

    @Column(name = "provider_code", nullable = false, length = 40)
    private String providerCode; // 'LOCAL' hoy

    @Column(name = "session_id", nullable = false, length = 100, unique = true)
    private String sessionId;    // UUID propio o del vendor

    // FK a identity.kyc_status
    @Column(name = "kyc_status_id", nullable = false)
    private Integer kycStatusId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "validated_at")
    private Date validatedAt;

    @Column(name = "liveness_passed")
    private Boolean livenessPassed;

    @Column(name = "returned_full_name", length = 150)
    private String returnedFullName;

    @Column(name = "returned_birth_date")
    private LocalDate returnedBirthDate;

    @Column(name = "evidence_stored", nullable = false)
    private boolean evidenceStored = false;

    public KycVerification(Person person,
                           String providerCode,
                           String sessionId,
                           Integer kycStatusId) {
        if (person == null) throw new IllegalArgumentException("person required");
        if (providerCode == null || providerCode.isBlank()) throw new IllegalArgumentException("provider_code required");
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("session_id required");
        if (kycStatusId == null) throw new IllegalArgumentException("kyc_status_id required");

        this.person = person;
        this.providerCode = providerCode.trim();
        this.sessionId = sessionId.trim();
        this.kycStatusId = kycStatusId;
    }

    public void markPassed(Date when) {
        this.kycStatusId = this.kycStatusId; // la app pondrá el id correcto (PASSED)
        this.validatedAt = when;
        this.livenessPassed = Boolean.TRUE; // opcional según vendor
    }
}
