package com.app.redcarga.tracking.interfaces.rest.responses;

public record CurrentLocationResponse(
        Integer quoteId,
        Integer driverId,
        double lat,
        double lng,
        Double speed,
        String updatedAt
) {}
