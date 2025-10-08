package com.app.redcarga.fleet.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class LicenseNumber {

    @Column(name = "license_number", length = 32)
    private String value;

    protected LicenseNumber() {}

    private LicenseNumber(String v) { this.value = v; }

    public static LicenseNumber of(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("license_required");
        String v = raw.trim().toUpperCase();
        if (v.length() < 3 || v.length() > 32) throw new IllegalArgumentException("license_length");
        if (!v.matches("^[A-Z0-9-]+$")) throw new IllegalArgumentException("license_invalid");
        return new LicenseNumber(v);
    }

    public String value() { return value; }

    @Override public String toString() { return value; }
}


