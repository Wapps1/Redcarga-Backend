package com.app.redcarga.tracking.interfaces.ws.messages;

public record LocationBroadcast(
    int quoteId,
    int driverId,
    double lat,
    double lng,
    Double speed,
    String timestamp
) {}
