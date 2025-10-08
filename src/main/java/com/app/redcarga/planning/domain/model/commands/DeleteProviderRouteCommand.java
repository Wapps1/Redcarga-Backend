package com.app.redcarga.planning.domain.model.commands;

/** Intención de eliminar una ruta existente. */
public record DeleteProviderRouteCommand(
        Integer routeId,
        Integer companyId
) {}


