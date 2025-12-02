package com.app.redcarga.media.interfaces.rest;

import com.app.redcarga.media.application.internal.commandservices.MediaUploadService;
import com.app.redcarga.media.interfaces.rest.responses.UploadImageResponse;
import com.app.redcarga.media.interfaces.rest.responses.UploadPdfResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@SecurityRequirement(name = "iam")
@RequiredArgsConstructor
public class MediaController {

    private final MediaUploadService service;

    @PostMapping(path = "/uploads:image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sube una imagen a Cloudinary (server-side)")
    public ResponseEntity<UploadImageResponse> uploadImage(
            @RequestParam("file") @NotNull MultipartFile file
    ) throws Exception {
        
        UploadImageResponse response = service.uploadImage(file);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/uploads:pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sube un PDF a ImageKit (server-side)")
    public ResponseEntity<UploadPdfResponse> uploadPdf(
            @RequestParam("file") @NotNull MultipartFile file
    ) throws Exception {
        UploadPdfResponse response = service.uploadPdf(file);
        return ResponseEntity.ok(response);
    }
}
