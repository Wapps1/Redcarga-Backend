package com.app.redcarga.tracking.domain.services;

import com.app.redcarga.tracking.interfaces.rest.responses.CurrentLocationResponse;

import java.util.Optional;

public interface TrackingQueryService {
    Optional<CurrentLocationResponse> getCurrentLocationByQuoteId(int quoteId);
}
