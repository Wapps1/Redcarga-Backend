package com.app.redcarga.admingeo.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * UBIGEO de provincia: 4 dígitos (ej. "1508" => dep "15").
 * Acepta "508" y normaliza a "0508".
 */
@Getter
@EqualsAndHashCode
@Embeddable
public class ProvinceCode {

    @Column(name = "province_code", length = 4)
    private String value;

    /** JPA */
    protected ProvinceCode() {}

    private ProvinceCode(String normalized) { this.value = normalized; }

    public static ProvinceCode of(String raw) {
        String v = normalize(raw, 4, "province_code_invalid");
        return new ProvinceCode(v);
    }

    public static ProvinceCode ofNullable(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return of(raw);
    }

    /** Prefijo DD como DepartmentCode. */
    public DepartmentCode departmentCode() {
        return DepartmentCode.of(value.substring(0, 2));
    }

    @Override public String toString() { return value; }

    // Helpers
    private static String normalize(String raw, int width, String errorCode) {
        if (raw == null) throw new IllegalArgumentException(errorCode);
        String t = raw.trim();
        if (t.isEmpty()) throw new IllegalArgumentException(errorCode);
        if (!t.chars().allMatch(Character::isDigit)) throw new IllegalArgumentException(errorCode);
        if (t.length() > width) throw new IllegalArgumentException(errorCode);
        String padded = ("0".repeat(width) + t);
        return padded.substring(padded.length() - width);
    }
}
