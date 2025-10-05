package com.app.redcarga.admingeo.domain.model.valueobjects;

import java.util.List;

/** VO compuesto para devolver el catálogo completo. */
public record GeoCatalog(
        List<DepartmentEntry> departments,
        List<ProvinceEntry> provinces
) {
    public GeoCatalog {
        if (departments == null) throw new IllegalArgumentException("departments_required");
        if (provinces == null) throw new IllegalArgumentException("provinces_required");
    }
}
