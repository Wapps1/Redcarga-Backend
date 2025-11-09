package com.app.redcarga.deals.interfaces.rest.responses;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageDto(
        Integer messageId,
        Integer quoteId,
        String typeCode,
        String contentCode,
        String body,
        String mediaUrl,
        UUID clientDedupKey,
        Integer createdBy,
        Instant createdAt
) {}
