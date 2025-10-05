package com.app.redcarga.planning.infrastructure.persistence.jdbc;

import com.app.redcarga.planning.application.internal.gateways.RouteTypeCatalogGateway;
import com.app.redcarga.planning.domain.model.valueobjects.RouteShape;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PgRouteTypeCatalogGateway implements RouteTypeCatalogGateway {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<RouteShape> findShapeByRouteTypeId(Integer routeTypeId) {
        if (routeTypeId == null || routeTypeId <= 0) return Optional.empty();

        String sql = """
            select code
            from planning.route_types
            where route_type_id = :id
        """;

        var codes = jdbc.query(sql, Map.of("id", routeTypeId),
                (rs, rowNum) -> rs.getString("code"));

        if (codes.isEmpty()) return Optional.empty();

        String code = codes.get(0);
        return Optional.of(mapCodeToShape(code));
    }

    private RouteShape mapCodeToShape(String code) {
        if (code == null) throw new IllegalArgumentException("route_type_code_null");
        return switch (code.trim().toUpperCase()) {
            case "DD" -> RouteShape.DD;
            case "PP" -> RouteShape.PP;
            default    -> throw new IllegalArgumentException("route_type_code_unknown");
        };
    }
}
