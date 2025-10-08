package com.app.redcarga.planning.domain.model.commands;

/**
 * Intención de actualizar una ruta existente. Permite cambiar shape a partir de routeTypeId.
 * Las provincias deben venir ambas nulas para DD o ambas presentes para PP.
 */
public record UpdateProviderRouteCommand(
        Integer routeId,
        Integer companyId,
        Integer routeTypeId,
        String originDepartmentCode,
        String destDepartmentCode,
        String originProvinceCode,
        String destProvinceCode,
        Boolean active
) {}


