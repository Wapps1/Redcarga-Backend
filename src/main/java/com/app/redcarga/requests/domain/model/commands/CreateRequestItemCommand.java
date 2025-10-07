package com.app.redcarga.requests.domain.model.commands;

import java.math.BigDecimal;

public record CreateRequestItemCommand(
        String itemName,
        BigDecimal heightCm,
        BigDecimal widthCm,
        BigDecimal lengthCm,
        BigDecimal weightKg,
        BigDecimal totalWeightKg,
        Integer quantity,
        boolean fragile,
        String notes
) {}
