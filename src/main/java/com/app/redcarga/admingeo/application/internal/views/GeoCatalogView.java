package com.app.redcarga.admingeo.application.internal.views;

import java.util.List;

public record GeoCatalogView(
        List<DepartmentView> departments,
        List<ProvinceView> provinces
) {}
