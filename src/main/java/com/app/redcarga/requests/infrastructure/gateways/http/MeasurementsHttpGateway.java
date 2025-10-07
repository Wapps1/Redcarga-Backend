package com.app.redcarga.requests.infrastructure.gateways.http;

import com.app.redcarga.requests.application.internal.gateways.MeasurementsEstimate;
import com.app.redcarga.requests.application.internal.gateways.MeasurementsGateway;
import com.app.redcarga.requests.application.internal.gateways.Upload;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MeasurementsHttpGateway implements MeasurementsGateway {

    private final WebClient measurementsWebClient;

    public MeasurementsHttpGateway(
            @Qualifier("measurementsWebClient") WebClient measurementsWebClient) {
        this.measurementsWebClient = measurementsWebClient;
    }

    @Override
    public MeasurementsEstimate estimate(Upload top, Upload side, double markerSizeMm, String requestId) {
        if (top == null && side == null) {
            throw new IllegalArgumentException("at_least_one_photo_required");
        }

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("marker_size_mm", Double.toString(markerSizeMm));

        if (requestId != null && !requestId.isBlank()) {
            builder.part("request_id", requestId);
        }

        addPhoto(builder, "top_photo", top);
        addPhoto(builder, "side_photo", side);

        return measurementsWebClient.post()
                .uri("/estimate")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(MeasurementsEstimate.class)
                .block(); // timeouts controlados por el HttpClient
    }

    private static void addPhoto(MultipartBodyBuilder builder, String name, Upload upload) {
        if (upload == null) return;

        builder.part(name, upload.bytes())
                .filename(upload.filename())
                .contentType(MediaType.IMAGE_JPEG);
    }
}
