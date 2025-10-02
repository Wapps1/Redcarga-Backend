package com.app.redcarga.providers.interfaces.rest;


import com.app.redcarga.shared.domain.exceptions.DomainException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ProvidersExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String,Object>> onDomain(DomainException ex) {
        String code = ex.getMessage() != null ? ex.getMessage() : "domain_error";
        HttpStatus status = switch (code) {
            case "account_not_found" -> NOT_FOUND; // 404 (snapshot IAM)
            case "invalid_provider_role" -> FORBIDDEN; // 403
            case "signup_step_not_ready" -> UNPROCESSABLE_ENTITY; // 422
            case "company_already_exists_for_account", "ruc_already_in_use" -> CONFLICT; // 409
            default -> BAD_REQUEST; // 400
        };
        return body(status, code);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> onAccessDenied(AccessDeniedException ex) {
        // ownership guard u otros checks de acceso
        String code = ex.getMessage() != null ? ex.getMessage() : "forbidden";
        return body(FORBIDDEN, code);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<Map<String,Object>> onValidation(Exception ex) {
        return body(BAD_REQUEST, "validation_error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> onIllegalArg(IllegalArgumentException ex) {
        // errores de VOs (Ruc/Phone/Address) → 422 genérico en este BC
        return body(UNPROCESSABLE_ENTITY, "invalid_company_data");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> onUnexpected(Exception ex) {
        return body(INTERNAL_SERVER_ERROR, "unexpected_error");
    }

    private ResponseEntity<Map<String,Object>> body(HttpStatus status, String code) {
        Map<String,Object> payload = new LinkedHashMap<>();
        payload.put("error", code);
        payload.put("status", status.value());
        payload.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(payload);
    }
}
