package com.app.redcarga.media.application.internal.commandservices;

import com.app.redcarga.media.infrastructure.cloudinary.CloudinaryUploadAdapter;
import com.app.redcarga.media.infrastructure.cloudinary.CloudinaryUploadAdapter.UploadResult;
import com.app.redcarga.media.infrastructure.imagekit.ImageKitUploadAdapter;
import com.app.redcarga.media.interfaces.rest.responses.UploadImageResponse;
import com.app.redcarga.media.interfaces.rest.responses.UploadPdfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MediaUploadService {

    private final CloudinaryUploadAdapter cloudinary;
    private final ImageKitUploadAdapter imageKit;

    /**
     * Sube una imagen a Cloudinary y retorna la URL pública.
     * 
     * @param file   archivo a subir
     * @return Respuesta con publicId y secureUrl
     */
    public UploadImageResponse uploadImage(MultipartFile file) {
        validateFile(file);
        
        try {
            byte[] bytes = file.getBytes();
            UploadResult result = cloudinary.upload(bytes, "requests", "image");
            return new UploadImageResponse(result.publicId(), result.secureUrl());
        } catch (IOException e) {
            throw new IllegalArgumentException("file_read_error", e);
        }
    }


    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file_required");
        }
        
        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("invalid_file_type");
        }
        
        // Validar tamaño (5MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("file_too_large");
        }
    }

    public UploadPdfResponse uploadPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file_required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("invalid_file_type_pdf");
        }

        // Allow larger size for PDFs (20MB)
        if (file.getSize() > 20L * 1024L * 1024L) {
            throw new IllegalArgumentException("file_too_large");
        }

        try {
            byte[] bytes = file.getBytes();
            return imageKit.upload(bytes, file.getOriginalFilename());
        } catch (IOException e) {
            throw new IllegalArgumentException("file_read_error", e);
        }
    }
}
