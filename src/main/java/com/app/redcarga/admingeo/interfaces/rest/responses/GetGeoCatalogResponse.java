package com.app.redcarga.admingeo.interfaces.rest.responses;

import java.util.List;

public record GetGeoCatalogResponse(
        List<DepartmentItemResponse> departments,
        List<ProvinceItemResponse> provinces
) {}
