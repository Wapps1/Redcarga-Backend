package com.app.redcarga.identity.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "identity", name = "verification_attempts",
        indexes = {
                @Index(name = "ix_attempts_ver", columnList = "verification_id")
        })
public class VerificationAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verification_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_attempt_verification"))
    private KycVerification verification;

    @Column(name = "provider_code", nullable = false, length = 40)
    private String providerCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_meta", columnDefinition = "jsonb")
    private Map<String, Object> requestMeta;

    @Column(name = "result", nullable = false, length = 10)
    private String result; // 'OK' | 'FAIL'

    @Column(name = "response_code", length = 50)
    private String responseCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    public VerificationAttempt(KycVerification verification,
                               String providerCode,
                               Map<String,Object> requestMeta,
                               String result,
                               String responseCode,
                               Date createdAt) {
        if (verification == null) throw new IllegalArgumentException("verification required");
        if (providerCode == null || providerCode.isBlank()) throw new IllegalArgumentException("provider_code required");
        if (!"OK".equals(result) && !"FAIL".equals(result)) throw new IllegalArgumentException("result must be OK or FAIL");
        if (createdAt == null) throw new IllegalArgumentException("createdAt required");

        this.verification = verification;
        this.providerCode = providerCode.trim();
        this.requestMeta = requestMeta;
        this.result = result;
        this.responseCode = responseCode;
        this.createdAt = createdAt;
    }
}
