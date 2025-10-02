package com.app.redcarga.providers.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Ruc {

    @Column(name = "ruc", nullable = false, length = 20)
    private String value;

    protected Ruc() {} // JPA

    private Ruc(String value) { this.value = value; }

    public static Ruc of(String raw) {
        if (raw == null) throw new IllegalArgumentException("ruc_required");
        String v = raw.trim();
        if (!v.matches("\\d{11}")) throw new IllegalArgumentException("ruc_invalid");
        return new Ruc(v);
    }

    @Override public String toString() { return value; }
}
