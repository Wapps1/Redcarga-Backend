package com.app.redcarga.deals.interfaces.rest.responses;

import java.time.Instant;

public record ChecklistItemResponse(
    Integer instanceItemId,
    Integer instanceId,
    String code,
    String statusCode,
    Integer completedBy,
    Instant completedAt
) {}
