package com.app.redcarga.planning.domain.model.commands;

import com.app.redcarga.planning.domain.model.valueobjects.RouteShape;

/** Intención de registrar una ruta; validación fina vive en el aggregate. */
public record RegisterProviderRouteCommand(
        Integer companyId,
        Integer routeTypeId,
        RouteShape shape,              // DD o PP
        String originDepartmentCode,
        String destDepartmentCode,
        String originProvinceCode,     // null si DD
        String destProvinceCode,       // null si DD
        Boolean active                 // opcional; default true
) {}
