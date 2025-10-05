package com.app.redcarga.planning.application.internal.gateways;

import com.app.redcarga.planning.domain.model.valueobjects.RouteShape;

import java.util.Optional;

/** Puerto para resolver el shape (DD/PP) a partir de route_type_id. Implementación en infra. */
public interface RouteTypeCatalogGateway {

    /** @return shape si existe; vacío si el route_type_id no está registrado. */
    Optional<RouteShape> findShapeByRouteTypeId(Integer routeTypeId);

    /** Helper para exigir existencia con message key consistente. */
    default RouteShape requireShapeByRouteTypeId(Integer routeTypeId) {
        return findShapeByRouteTypeId(routeTypeId)
                .orElseThrow(() -> new IllegalArgumentException("route_type_not_found"));
    }
}
