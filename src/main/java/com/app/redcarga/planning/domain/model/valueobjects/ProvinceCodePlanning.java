package com.app.redcarga.planning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Código de provincia (4 dígitos, prefijo = departamento, e.g., "1501"). */
@Getter
@EqualsAndHashCode
@Embeddable
public class ProvinceCodePlanning {

    @Column(length = 4)
    private String value;

    protected ProvinceCodePlanning() { /* JPA */ }

    private ProvinceCodePlanning(String v) { this.value = v; }

    public static ProvinceCodePlanning of(String raw) {
        String v = normalize(raw);
        if (v.length() != 4 || !v.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("province_code_invalid");
        return new ProvinceCodePlanning(v);
    }

    private static String normalize(String raw) {
        if (raw == null) throw new IllegalArgumentException("province_code_required");
        String v = raw.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("province_code_required");
        return v;
    }

    @Override public String toString() { return value; }
}
