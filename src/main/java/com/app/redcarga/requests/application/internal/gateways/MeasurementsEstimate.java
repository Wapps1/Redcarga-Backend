package com.app.redcarga.requests.application.internal.gateways;
public record MeasurementsEstimate(
        String method,
        View top,
        View side,
        Dimensions dimensions_cm
) {
    public record View(
            Boolean ok,
            Double width_mm,
            Double length_mm,
            Double height_mm,
            Double marker_px,
            Double blur,
            Double confidence,
            Boolean coplanar,
            String quality,
            String reason
    ) {}
    public record Dimensions(
            double width_cm,
            double length_cm,
            double height_cm,
            double confidence
    ) {}
}
