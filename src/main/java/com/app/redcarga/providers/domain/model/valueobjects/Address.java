package com.app.redcarga.providers.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Address {

    @Column(name = "address", length = 400)
    private String value;

    protected Address() {} // JPA

    private Address(String v) { this.value = v; }

    public static Address ofNullable(String raw) {
        if (raw == null || raw.isBlank()) return new Address(null);
        String v = raw.trim();
        if (v.length() > 400) throw new IllegalArgumentException("address_too_long");
        return new Address(v);
    }

    @Override public String toString() { return value; }
}
