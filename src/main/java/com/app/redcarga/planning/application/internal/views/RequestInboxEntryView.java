package com.app.redcarga.planning.application.internal.views;

import java.time.Instant;

public record RequestInboxEntryView(
    int requestId,
    int companyId,
    Integer matchedRouteId,
    Integer routeTypeId,
    String status,
    Instant createdAt,
    String requesterName,
    String originDepartmentName,
    String originProvinceName,
    String destDepartmentName,
    String destProvinceName,
    Integer totalQuantity
) {}