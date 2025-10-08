package com.app.redcarga.requests.application.internal.views;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record RequestDetailView(
        Integer requestId,
        Integer requesterAccountId,
        String requesterName,   // snapshot o fresco si includeFreshIdentity
        String requesterDni,    // solo si includeFreshIdentity
        String status,          // OPEN | CLOSED | CANCELLED | EXPIRED
        Instant createdAt,
        Instant updatedAt,
        Instant closedAt,
        LocationView origin,
        LocationView destination,
        Integer itemsCount,
        BigDecimal totalWeightKg,
        List<RequestItemView> items,
        Boolean paymentOnDelivery
) {}
