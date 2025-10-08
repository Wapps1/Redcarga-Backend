package com.app.redcarga.requests.interfaces.rest.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CreateRequestRequest(
        @NotNull @Valid UbigeoSnapshotRequest origin,
        @NotNull @Valid UbigeoSnapshotRequest destination,
        @NotNull Boolean paymentOnDelivery,
        String request_name,
        @NotNull @Size(min = 1) List<@Valid CreateRequestItemRequest> items
) {
    public record UbigeoSnapshotRequest(
            @NotBlank @Size(min = 2, max = 2) String departmentCode,
            @NotBlank @Size(max = 100) String departmentName,
            @Size(min = 4, max = 4) String provinceCode,        // opcional
            @Size(max = 100) String provinceName,               // requerido si hay provinceCode
            @Size(max = 150) String districtText                // opcional
    ) {
        public void validate() {
            if (provinceCode != null && (provinceName == null || provinceName.isBlank()))
                throw new IllegalArgumentException("province_name_required");
        }
    }

    public record CreateRequestItemRequest(
            @NotBlank @Size(max = 160) String itemName,
            @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 6, fraction = 2) BigDecimal heightCm,
            @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 6, fraction = 2) BigDecimal widthCm,
            @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 6, fraction = 2) BigDecimal lengthCm,
            @DecimalMin(value = "0.000", inclusive = true) @Digits(integer = 7, fraction = 3) BigDecimal weightKg,
            @DecimalMin(value = "0.000", inclusive = true) @Digits(integer = 7, fraction = 3) BigDecimal totalWeightKg,
            @NotNull @Min(1) Integer quantity,
            @NotNull Boolean fragile,
            @Size(max = 400) String notes,
            List<@Valid CreateRequestItemImageRequest> images // opcional
    ) {}

    public record CreateRequestItemImageRequest(
            @NotBlank @Size(max = 2048) String imageUrl,
            @Min(1) Integer imagePosition // opcional; si es null el aggregate autonumera
    ) {}
}
