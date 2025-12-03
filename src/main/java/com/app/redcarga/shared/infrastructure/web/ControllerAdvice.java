package com.app.redcarga.shared.infrastructure.web;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = base("validation_error", "Campos inválidos");
        Map<String, String> errors = new HashMap<>();
        for (var e : ex.getBindingResult().getAllErrors()) {
            String field = e instanceof FieldError fe ? fe.getField() : e.getObjectName();
            errors.put(field, e.getDefaultMessage());
        }
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConflict(DataIntegrityViolationException ex) {
        Map<String, Object> body = base("conflict", "Conflicto de datos (unicidad o FK)");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(base("unauthorized", "No autenticado"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccess(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(base("forbidden", "Acceso denegado"));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(DomainException ex) {
        String code = ex.getMessage() != null ? ex.getMessage() : "domain_error";
        HttpStatus status = switch (code) {
            case "not_authorized_for_quote" -> HttpStatus.FORBIDDEN;
            case "location_not_found" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(status).body(base(code, code));
    }

    // Hook para DomainException propia si la defines
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleDomain(IllegalArgumentException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(base("domain_error", ex.getMessage()));
    }

    private Map<String, Object> base(String code, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("code", code);
        m.put("message", message);
        return m;
    }
}
