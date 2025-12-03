package com.app.redcarga.tracking.domain.services;

public interface TrackingCommandService {
    void updateLocation(int quoteId, int driverId, double lat, double lng);
}
