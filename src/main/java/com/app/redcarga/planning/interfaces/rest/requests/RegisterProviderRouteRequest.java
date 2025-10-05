package com.app.redcarga.planning.interfaces.rest.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Datos para registrar una ruta de proveedor (company)")
public record RegisterProviderRouteRequest(

        @NotNull
        @Schema(description = "ID del tipo de ruta (catálogo planning.route_types). DD=1, PP=2 (seed).",
                example = "1")
        Integer routeTypeId,

        @NotNull
        @Pattern(regexp = "\\d{2}", message = "originDepartmentCode_must_be_2_digits")
        @Schema(description = "UBIGEO departamento origen (2 dígitos).", example = "15")
        String originDepartmentCode,

        @NotNull
        @Pattern(regexp = "\\d{2}", message = "destDepartmentCode_must_be_2_digits")
        @Schema(description = "UBIGEO departamento destino (2 dígitos).", example = "08")
        String destDepartmentCode,

        @Pattern(regexp = "(^$|\\d{4})", message = "originProvinceCode_must_be_4_digits_or_empty")
        @Schema(description = "UBIGEO provincia origen (4 dígitos). Solo si routeType=PP.", example = "1501")
        String originProvinceCode,

        @Pattern(regexp = "(^$|\\d{4})", message = "destProvinceCode_must_be_4_digits_or_empty")
        @Schema(description = "UBIGEO provincia destino (4 dígitos). Solo si routeType=PP.", example = "0401")
        String destProvinceCode,

        @Schema(description = "Activa/inactiva. Default: true si es null.", example = "true")
        Boolean active
) {}
