package com.app.redcarga.identity.interfaces.rest;

import com.app.redcarga.shared.domain.exceptions.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class IdentityExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String,Object>> onDomain(DomainException ex) {
        String code = ex.getMessage() != null ? ex.getMessage() : "domain_error";
        HttpStatus status = switch (code) {
            case "account_not_found" -> NOT_FOUND;                      // 404
            case "document_already_exists", "signup_intent_invalid_state" -> CONFLICT; // 409
            case "invalid_doc_type", "underage", "invalid_document_format" -> UNPROCESSABLE_ENTITY; // 422
            default -> BAD_REQUEST;
        };
        return body(status, code);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> onIllegalArg(IllegalArgumentException ex) {
        // Cae aquí, por ejemplo, si DocNumber.of(...) revienta; lo mapeamos a 422 genérico
        return body(UNPROCESSABLE_ENTITY, "invalid_document_format");
    }

    private ResponseEntity<Map<String,Object>> body(HttpStatus status, String code) {
        Map<String,Object> payload = new LinkedHashMap<>();
        payload.put("error", code);
        payload.put("status", status.value());
        payload.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(payload);
    }
}
