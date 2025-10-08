package com.app.redcarga.fleet.domain.model.aggregates;

import com.app.redcarga.fleet.domain.model.valueobjects.LicenseNumber;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.shared.domain.model.valueobjects.Email;
import com.app.redcarga.shared.domain.model.valueobjects.Phone;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "fleet", name = "drivers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_drivers_company_license", columnNames = {"company_id", "license_number"})
        })
@AttributeOverride(name = "id", column = @Column(name = "driver_id"))
public class Driver extends AuditableAbstractAggregateRoot<Driver> {

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;

    @Column(name = "email", length = 200)
    private String email;

    @Embedded
    private Phone phone;

    @Column(name = "license_number", length = 32)
    private String licenseNumber;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public static Driver create(
            Integer companyId,
            String firstName,
            String lastName,
            String email,
            String phone,
            String licenseNumber,
            Boolean active
    ) {
        Driver d = new Driver();
        d.companyId = requirePositive(companyId, "companyId");
        d.firstName = normalizeRequired(firstName, 1, 120, "firstName");
        d.lastName = normalizeRequired(lastName, 1, 120, "lastName");
        d.email = normalizeEmail(email);
        d.phone = (phone != null && !phone.isBlank()) ? Phone.of(phone) : null;
        d.licenseNumber = (licenseNumber != null && !licenseNumber.isBlank()) ? LicenseNumber.of(licenseNumber).value() : null;
        d.active = (active != null) ? active : true;
        return d;
    }

    public void update(String firstName, String lastName, String email, String phone, String licenseNumber, Boolean active) {
        if (firstName != null && !firstName.isBlank()) this.firstName = normalizeRequired(firstName, 1, 120, "firstName");
        if (lastName != null && !lastName.isBlank()) this.lastName = normalizeRequired(lastName, 1, 120, "lastName");
        if (email != null) this.email = normalizeEmail(email);
        if (phone != null) this.phone = (!phone.isBlank()) ? Phone.of(phone) : null;
        if (licenseNumber != null) this.licenseNumber = (!licenseNumber.isBlank()) ? LicenseNumber.of(licenseNumber).value() : null;
        if (active != null) this.active = active;
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    private static String normalizeEmail(String value) {
        if (value == null || value.isBlank()) return null;
        return new Email(value).value();
    }

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
}


