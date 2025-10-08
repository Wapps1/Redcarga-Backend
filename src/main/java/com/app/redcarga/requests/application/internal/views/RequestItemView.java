package com.app.redcarga.requests.application.internal.views;

import java.math.BigDecimal;
import java.util.List;

public record RequestItemView(
        Integer itemId,
        String itemName,
        BigDecimal heightCm,
        BigDecimal widthCm,
        BigDecimal lengthCm,
        BigDecimal weightKg,
        BigDecimal totalWeightKg,
        Integer quantity,
        boolean fragile,
        String notes,
        Integer position,
        List<RequestImageView> images
) {}
