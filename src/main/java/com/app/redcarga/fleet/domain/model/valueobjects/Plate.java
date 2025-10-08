package com.app.redcarga.fleet.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Plate {

    @Column(name = "plate", nullable = false, length = 16)
    private String value;

    protected Plate() {}

    private Plate(String v) { this.value = v; }

    public static Plate of(String raw) {
        if (raw == null) throw new IllegalArgumentException("plate_required");
        String v = raw.trim().toUpperCase();
        if (v.length() < 3 || v.length() > 16) throw new IllegalArgumentException("plate_length");
        // validación laxa: letras/números/guiones
        if (!v.matches("^[A-Z0-9-]+$")) throw new IllegalArgumentException("plate_invalid");
        return new Plate(v);
    }

    public String value() { return value; }

    @Override public String toString() { return value; }
}


