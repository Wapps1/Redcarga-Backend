package com.app.redcarga.planning.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Código de departamento (2 dígitos, e.g., "15"). */
@Getter
@EqualsAndHashCode
@Embeddable
public class DepartmentCodePlanning {

    // No fijamos name aquí para poder sobreescribirlo por campo en el aggregate
    @Column(length = 2)
    private String value;

    protected DepartmentCodePlanning() { /* JPA */ }

    private DepartmentCodePlanning(String v) { this.value = v; }

    public static DepartmentCodePlanning of(String raw) {
        String v = normalize(raw);
        if (v.length() != 2 || !v.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("department_code_invalid");
        return new DepartmentCodePlanning(v);
    }

    private static String normalize(String raw) {
        if (raw == null) throw new IllegalArgumentException("department_code_required");
        String v = raw.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("department_code_required");
        return v;
    }

    @Override public String toString() { return value; }
}
