package com.app.redcarga.planning.interfaces.rest.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Payload to update a provider route (company)")
public record UpdateProviderRouteRequest(

        @NotNull
        @Schema(description = "Route type id (planning.route_types catalog). DD=1, PP=2 (seed).",
                example = "1")
        Integer routeTypeId,

        @NotNull
        @Pattern(regexp = "\\d{2}", message = "originDepartmentCode_must_be_2_digits")
        @Schema(description = "UBIGEO origin department (2 digits).", example = "15")
        String originDepartmentCode,

        @NotNull
        @Pattern(regexp = "\\d{2}", message = "destDepartmentCode_must_be_2_digits")
        @Schema(description = "UBIGEO destination department (2 digits).", example = "08")
        String destDepartmentCode,

        @Pattern(regexp = "(^$|\\d{4})", message = "originProvinceCode_must_be_4_digits_or_empty")
        @Schema(description = "UBIGEO origin province (4 digits). Only for routeType=PP.", example = "1501")
        String originProvinceCode,

        @Pattern(regexp = "(^$|\\d{4})", message = "destProvinceCode_must_be_4_digits_or_empty")
        @Schema(description = "UBIGEO destination province (4 digits). Only for routeType=PP.", example = "0401")
        String destProvinceCode,

        @Schema(description = "Active flag. Default: true if null.", example = "true")
        Boolean active
) {}


