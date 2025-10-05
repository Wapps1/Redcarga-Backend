package com.app.redcarga.admingeo.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * UBIGEO de departamento: 2 dígitos (ej. "15").
 * Acepta "5" y normaliza a "05".
 */
@Getter
@EqualsAndHashCode
@Embeddable
public class DepartmentCode {

    @Column(name = "department_code", length = 2)
    private String value;

    /** JPA */
    protected DepartmentCode() {}

    private DepartmentCode(String normalized) { this.value = normalized; }

    public static DepartmentCode of(String raw) {
        String v = normalize(raw, 2, "department_code_invalid");
        return new DepartmentCode(v);
    }

    public static DepartmentCode ofNullable(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return of(raw);
    }

    @Override public String toString() { return value; }

    // Helpers
    private static String normalize(String raw, int width, String errorCode) {
        if (raw == null) throw new IllegalArgumentException(errorCode);
        String t = raw.trim();
        if (t.isEmpty()) throw new IllegalArgumentException(errorCode);
        if (!t.chars().allMatch(Character::isDigit)) throw new IllegalArgumentException(errorCode);
        if (t.length() > width) throw new IllegalArgumentException(errorCode);
        // left pad con ceros hasta el ancho requerido
        String padded = ("0".repeat(width) + t);
        return padded.substring(padded.length() - width);
    }
}
