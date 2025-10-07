package com.app.redcarga.media.interfaces.rest;

import com.app.redcarga.media.application.internal.commandservices.MediaUploadCommandService;
import com.app.redcarga.media.domain.model.valueobjects.MediaSubjectType;
import com.app.redcarga.media.domain.model.valueobjects.SubjectRef;
import com.app.redcarga.media.domain.model.valueobjects.UploadFile;
import com.app.redcarga.media.interfaces.rest.responses.UploadImageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaUploadController {

    private final MediaUploadCommandService service;

    public MediaUploadController(MediaUploadCommandService service) {
        this.service = service;
    }

    @PostMapping(path = "/uploads:image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "iam")
    @Operation(summary = "Sube una imagen a Cloudinary (server-side)")
    public ResponseEntity<UploadImageResponse> uploadImage(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("subjectType") @NotNull MediaSubjectType subjectType,
            @RequestParam("subjectKey") @NotBlank String subjectKey
    ) throws Exception {
        String key = subjectKey.trim();

        UploadFile uf = UploadFile.of(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg",
                file.getContentType() != null ? file.getContentType() : "image/jpeg",
                file.getBytes()
        );

        var out = service.upload(SubjectRef.of(subjectType, key), uf);

        return ResponseEntity.ok(new UploadImageResponse(
                asStr(out.get("public_id")),
                asStr(out.get("secure_url"))
        ));
    }

    private static String asStr(Object o){ return o==null? null : o.toString(); }
}
