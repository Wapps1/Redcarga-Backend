package com.app.redcarga.requests.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record RequestDetailResponse(
        Integer requestId,
        Integer requesterAccountId,
        String requesterNameSnapshot,
        String requesterDocNumber,
        String status, // OPEN/CLOSED/CANCELLED/EXPIRED
        Instant createdAt,
        Instant updatedAt,
        Instant closedAt,
        UbigeoSnapshotResponse origin,
        UbigeoSnapshotResponse destination,
        Integer itemsCount,
        BigDecimal totalWeightKg,
        boolean paymentOnDelivery,
        List<RequestItemResponse> items
) {
    public record UbigeoSnapshotResponse(
            String departmentCode, String departmentName,
            String provinceCode,   String provinceName,
            String districtText
    ) {}
    public record RequestItemResponse(
            Integer itemId,
            String itemName,
            BigDecimal heightCm, BigDecimal widthCm, BigDecimal lengthCm,
            BigDecimal weightKg, BigDecimal totalWeightKg,
            Integer quantity, boolean fragile, String notes, Integer position,
            List<RequestItemImageResponse> images
    ) {}
    public record RequestItemImageResponse(Integer imageId, String imageUrl, Integer imagePosition) {}
}
