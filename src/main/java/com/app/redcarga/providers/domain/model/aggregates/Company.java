package com.app.redcarga.providers.domain.model.aggregates;

import com.app.redcarga.providers.domain.model.valueobjects.CompanyStatus;
import com.app.redcarga.providers.domain.model.valueobjects.Address;
import com.app.redcarga.shared.domain.model.valueobjects.Phone;
import com.app.redcarga.providers.domain.model.valueobjects.Ruc;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.shared.domain.model.valueobjects.Email;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "providers", name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_companies_ruc", columnNames = "ruc"),
                @UniqueConstraint(name = "uq_companies_created_by", columnNames = "created_by_account_id")
        })
@AttributeOverride(name = "id", column = @Column(name = "company_id"))
public class Company extends AuditableAbstractAggregateRoot<Company> {

    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Column(name = "trade_name", length = 200)
    private String tradeName;

    @Embedded
    private Ruc ruc;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Embedded
    private Phone phone;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CompanyStatus status;

    @Column(name = "created_by_account_id", nullable = false)
    private Integer createdByAccountId;

    /** Factory */
    public static Company createSubmitted(
            Integer createdByAccountId,
            String legalName,
            String tradeName,
            String ruc,
            String email,
            String phone,
            String address
    ) {
        Company c = new Company();
        c.createdByAccountId = requirePositive(createdByAccountId, "createdByAccountId");

        c.legalName = normalizeRequired(legalName, 1, 200, "legalName");
        c.tradeName = normalizeOptional(tradeName, 1, 200);

        c.ruc = Ruc.of(ruc);
        c.email = new Email(email).value();   // record de shared
        c.phone = Phone.of(phone);
        c.address = Address.ofNullable(address);

        c.status = CompanyStatus.SUBMITTED;
        return c;
    }

    public void markVerified() {
        if (status == CompanyStatus.DISABLED) throw new IllegalStateException("company_disabled");
        this.status = CompanyStatus.VERIFIED;
    }

    public void disable() {
        this.status = CompanyStatus.DISABLED;
    }

    // ===== helpers =====
    private static Integer requirePositive(Integer v, String field) {
        if (v == null || v <= 0) throw new IllegalArgumentException(field + "_invalid");
        return v;
    }

    private static String normalizeRequired(String raw, int min, int max, String field) {
        if (raw == null) throw new IllegalArgumentException(field + "_required");
        String v = raw.trim();
        if (v.length() < min || v.length() > max) throw new IllegalArgumentException(field + "_length");
        return v;
    }

    private static String normalizeOptional(String raw, int min, int max) {
        if (raw == null || raw.isBlank()) return null;
        String v = raw.trim();
        if (v.length() < min || v.length() > max) throw new IllegalArgumentException("length_invalid");
        return v;
    }
}
