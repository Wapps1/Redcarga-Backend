package com.app.redcarga.tracking.interfaces.ws.requests;

public record LocationUpdateRequest(
    double lat,
    double lng,
    Double speed
) {}
