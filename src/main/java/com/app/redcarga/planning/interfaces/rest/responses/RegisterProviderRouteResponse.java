package com.app.redcarga.planning.interfaces.rest.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado del registro de ruta")
public record RegisterProviderRouteResponse(
        boolean success,
        Integer routeId
) {}
