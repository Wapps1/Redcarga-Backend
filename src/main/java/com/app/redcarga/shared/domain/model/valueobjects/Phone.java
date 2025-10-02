package com.app.redcarga.shared.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Phone {

    @Column(name = "phone", nullable = false, length = 50)
    private String value;

    protected Phone() {} // JPA

    private Phone(String v) { this.value = v; }

    public static Phone of(String raw) {
        if (raw == null) throw new IllegalArgumentException("phone_required");
        String v = raw.trim();
        if (!v.matches("^\\+?[\\d\\s()-]{6,20}$")) throw new IllegalArgumentException("phone_invalid");
        return new Phone(v);
    }

    @Override public String toString() { return value; }
}
