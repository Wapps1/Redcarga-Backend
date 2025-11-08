package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.persistence.OptimisticLockException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class DealsExceptionHandler {

    private ResponseEntity<Object> error(String code, HttpStatus status, Object details) {
        return ResponseEntity.status(status).body(
                Map.of("error", code, "status", status.value(), "timestamp", Instant.now().toString(), "details", details)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> onValidation(MethodArgumentNotValidException ex) {
        var fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage(),
                        "rejectedValue", fe.getRejectedValue()
                )).collect(Collectors.toList());
        return error("validation_error", HttpStatus.BAD_REQUEST, fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> onConstraint(ConstraintViolationException ex) {
        var details = ex.getConstraintViolations().stream()
                .map(v -> Map.of("path", v.getPropertyPath().toString(), "message", v.getMessage()))
                .collect(Collectors.toList());
        return error("validation_error", HttpStatus.BAD_REQUEST, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> onUnreadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof MismatchedInputException mie) {
            List<JsonMappingException.Reference> path = mie.getPath();
            String field = path.isEmpty() ? null : path.stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));
            String target = mie.getTargetType() != null ? mie.getTargetType().getSimpleName() : "value";
            var detail = Map.of(
                    "field", field,
                    "message", "invalid_type",
                    "expected", target,
                    "originalMessage", mie.getOriginalMessage()
            );
            return error("invalid_request_body", HttpStatus.BAD_REQUEST, List.of(detail));
        }
        return error("invalid_request_body", HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> onAccess(AccessDeniedException ex) {
        return error("access_denied", HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockException.class})
    public ResponseEntity<Object> onOptimistic(Exception ex) {
        return error("conflict_version", HttpStatus.CONFLICT, "resource_modified");
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> onDomain(DomainException ex) {
        String key = ex.getMessage();
        return switch (key) {
            case "account_not_found" -> error(key, HttpStatus.NOT_FOUND, null);
            case "invalid_provider_role", "company_not_member", "not_member_of_company" -> error(key, HttpStatus.FORBIDDEN, null);
            case "quote_not_found" -> error(key, HttpStatus.NOT_FOUND, null);
            default -> error(key != null ? key : "domain_error", HttpStatus.BAD_REQUEST, null);
        };
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> onIllegalArg(IllegalArgumentException ex) {
        return error("invalid_input", HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> onOther(Exception ex) {
        return error("internal_error", HttpStatus.INTERNAL_SERVER_ERROR, "unexpected");
    }
}