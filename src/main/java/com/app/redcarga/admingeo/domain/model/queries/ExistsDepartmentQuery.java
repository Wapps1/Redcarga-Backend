package com.app.redcarga.admingeo.domain.model.queries;

import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentCode;

/** Consulta de existencia para un código de departamento. */
public record ExistsDepartmentQuery(DepartmentCode code) {
    public ExistsDepartmentQuery {
        if (code == null) throw new IllegalArgumentException("department_code_required");
    }
}
