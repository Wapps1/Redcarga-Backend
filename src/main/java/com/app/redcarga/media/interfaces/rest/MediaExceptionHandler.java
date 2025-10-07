package com.app.redcarga.media.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice(assignableTypes = MediaUploadController.class)
public class MediaExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<?> handleCloudinary(HttpStatusCodeException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getResponseBodyAsString()
        ));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<?> handleNetwork(ResourceAccessException ex) {
        return ResponseEntity.status(502).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 502,
                "error", "upstream_unreachable",
                "message", ex.getMostSpecificCause()!=null? ex.getMostSpecificCause().getMessage(): ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "internal_error",
                "message", ex.getMessage()
        ));
    }
}
