package com.app.redcarga.requests.application.internal.gateways;

public interface MeasurementsGateway {
    /**
     * Llama al sidecar /estimate. top y/o side pueden ser null (pero no ambos).
     * markerSizeMm típicamente 100. requestId es opcional, para correlación.
     */
    MeasurementsEstimate estimate(Upload top, Upload side, double markerSizeMm, String requestId);
}
