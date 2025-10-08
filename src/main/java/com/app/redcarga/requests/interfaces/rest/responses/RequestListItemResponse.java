package com.app.redcarga.requests.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.Instant;

public record RequestListItemResponse(
        Integer requestId,
        String requestName,
        String status,          // OPEN | CLOSED | CANCELLED | EXPIRED
        Instant createdAt,
        Instant updatedAt,
        Instant closedAt,
        LocationResponse origin,
        LocationResponse destination,
        Integer itemsCount,
        BigDecimal totalWeightKg,
        Boolean paymentOnDelivery
) {
    public record LocationResponse(
            String departmentCode, String departmentName,
            String provinceCode,   String provinceName,
            String districtText
    ) {}
}
