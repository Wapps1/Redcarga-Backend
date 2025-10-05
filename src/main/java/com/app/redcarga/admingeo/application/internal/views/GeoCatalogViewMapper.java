package com.app.redcarga.admingeo.application.internal.views;

import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentEntry;
import com.app.redcarga.admingeo.domain.model.valueobjects.GeoCatalog;
import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceEntry;

import java.util.stream.Collectors;

public final class GeoCatalogViewMapper {
    private GeoCatalogViewMapper() {}

    public static GeoCatalogView toView(GeoCatalog geo) {
        var deps = geo.departments().stream()
                .map(GeoCatalogViewMapper::mapDept)
                .collect(Collectors.toList());

        var provs = geo.provinces().stream()
                .map(GeoCatalogViewMapper::mapProv)
                .collect(Collectors.toList());

        return new GeoCatalogView(deps, provs);
    }

    private static DepartmentView mapDept(DepartmentEntry d) {
        return new DepartmentView(d.getCode().getValue(), d.getName());
    }

    private static ProvinceView mapProv(ProvinceEntry p) {
        return new ProvinceView(
                p.getCode().getValue(),
                p.getDepartmentCode().getValue(),
                p.getName()
        );
    }
}
