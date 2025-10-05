package com.app.redcarga.admingeo.domain.model.queries;

import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceCode;

/** Consulta de existencia para un código de provincia. */
public record ExistsProvinceQuery(ProvinceCode code) {
    public ExistsProvinceQuery {
        if (code == null) throw new IllegalArgumentException("province_code_required");
    }
}
