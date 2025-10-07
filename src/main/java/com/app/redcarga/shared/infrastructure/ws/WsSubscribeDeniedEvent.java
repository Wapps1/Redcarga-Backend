package com.app.redcarga.shared.infrastructure.ws;

public record WsSubscribeDeniedEvent(
        String userId,
        String reason,
        String destination,
        long timestamp
) {}
