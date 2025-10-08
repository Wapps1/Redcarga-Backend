package com.app.redcarga.requests.interfaces.rest;

import com.app.redcarga.requests.application.internal.gateways.Upload;
import com.app.redcarga.requests.application.internal.queryservices.DimensionsEstimationQueryService;
import com.app.redcarga.requests.interfaces.rest.responses.EstimateDimensionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/requests/dimensions")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class DimensionsEstimationController {

    private final DimensionsEstimationQueryService service;

    @Operation(summary = "Estimar dimensiones a partir de fotos (sidecar)")
    @PostMapping(path=":estimate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EstimateDimensionsResponse> estimate(
            @RequestPart(required = false) @Nullable MultipartFile top_photo,
            @RequestPart(required = false) @Nullable MultipartFile side_photo,
            @RequestParam(defaultValue = "100") double marker_size_mm,
            @RequestParam(required = false) @Nullable String request_id
    ) throws Exception {

        // Al menos una foto
        if ((top_photo == null || top_photo.isEmpty()) &&
                (side_photo == null || side_photo.isEmpty())) {
            return ResponseEntity.unprocessableEntity().build(); // tu ExceptionHandler puede mapear mejor si prefieres
        }

        var top  = toUpload(top_photo);
        var side = toUpload(side_photo);

        var view = service.estimate(top, side, marker_size_mm, request_id);

        return ResponseEntity.ok(new EstimateDimensionsResponse(
                view.width_cm(), view.length_cm(), view.height_cm()
        ));
    }

    private static Upload toUpload(@Nullable MultipartFile f) throws Exception {
        if (f == null || f.isEmpty()) return null;
        return Upload.of(f.getOriginalFilename(), f.getBytes());
    }
}
