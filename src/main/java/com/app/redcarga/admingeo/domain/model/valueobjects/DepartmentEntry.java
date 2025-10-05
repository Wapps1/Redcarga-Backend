package com.app.redcarga.admingeo.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Item de catálogo: Departamento (DD + nombre). */
@Getter
@EqualsAndHashCode
public final class DepartmentEntry {
    private final DepartmentCode code;
    private final String name;

    public DepartmentEntry(DepartmentCode code, String name) {
        if (code == null) throw new IllegalArgumentException("department_code_required");
        String n = normalizeName(name, "department_name_invalid");
        this.code = code;
        this.name = n;
    }

    private static String normalizeName(String raw, String error) {
        if (raw == null) throw new IllegalArgumentException(error);
        String t = raw.trim();
        if (t.isEmpty()) throw new IllegalArgumentException(error);
        return t;
    }

    @Override public String toString() { return code.getValue() + " - " + name; }
}
