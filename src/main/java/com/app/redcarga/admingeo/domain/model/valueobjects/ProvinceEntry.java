package com.app.redcarga.admingeo.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Item de catálogo: Provincia (DDPP + DD + nombre). */
@Getter
@EqualsAndHashCode
public final class ProvinceEntry {
    private final ProvinceCode code;
    private final DepartmentCode departmentCode;
    private final String name;

    public ProvinceEntry(ProvinceCode code, DepartmentCode departmentCode, String name) {
        if (code == null) throw new IllegalArgumentException("province_code_required");
        if (departmentCode == null) throw new IllegalArgumentException("department_code_required");
        // coherencia prefijo: DD de province = departmentCode
        if (!code.getValue().startsWith(departmentCode.getValue()))
            throw new IllegalArgumentException("province_prefix_mismatch");
        String n = normalizeName(name, "province_name_invalid");
        this.code = code;
        this.departmentCode = departmentCode;
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
