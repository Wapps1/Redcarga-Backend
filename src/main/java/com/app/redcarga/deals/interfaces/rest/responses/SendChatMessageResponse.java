package com.app.redcarga.deals.interfaces.rest.responses;

import java.time.Instant;

public record SendChatMessageResponse(
        boolean ok,
        Integer messageId,
        Instant createdAt
) {}
