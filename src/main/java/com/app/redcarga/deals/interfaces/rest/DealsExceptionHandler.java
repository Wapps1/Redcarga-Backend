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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.LinkedHashMap;
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
            case "not_participant_of_quote", "not_request_owner", "not_chat_participant", "chat_state_not_allowed" -> error(key, HttpStatus.FORBIDDEN, null);
            case "kind_required", "kind_invalid", "text_required", "text_empty", "text_too_long",
                 "image_url_required", "image_url_too_long", "caption_too_long",
                 "content_kind_invalid", "dedup_invalid" -> error(key, HttpStatus.UNPROCESSABLE_ENTITY, null);
            case "limit_invalid", "if_match_invalid" -> error(key, HttpStatus.BAD_REQUEST, null);
            case "quote_not_found" -> error(key, HttpStatus.NOT_FOUND, null);
            case "quote_not_accepted" -> error(key, HttpStatus.UNPROCESSABLE_ENTITY, null);
            case "checklist_instance_not_found", "checklist_item_assignment_missing" -> error(key, HttpStatus.NOT_FOUND, null);
            case "assignment_not_found" -> error(key, HttpStatus.NOT_FOUND, null);
            case "assignment_version_mismatch", "assignment_optimistic_lock" -> error(key, HttpStatus.CONFLICT, null);
            default -> error(key != null ? key : "domain_error", HttpStatus.BAD_REQUEST, null);
        };
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> onIllegalArg(IllegalArgumentException ex) {
        return error("invalid_input", HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(com.app.redcarga.deals.domain.exceptions.ChecklistDependencyException.class)
    public ResponseEntity<Object> onChecklistDependency(com.app.redcarga.deals.domain.exceptions.ChecklistDependencyException ex) {
        return error(ex.getCode(), HttpStatus.UNPROCESSABLE_ENTITY, ex.getMissing());
    }

    @ExceptionHandler(com.app.redcarga.deals.domain.exceptions.GuideAlreadyExistsException.class)
    public ResponseEntity<Object> onGuideAlreadyExists(com.app.redcarga.deals.domain.exceptions.GuideAlreadyExistsException ex) {
        var details = Map.of(
                "quoteId", ex.getQuoteId(),
                "type", ex.getType().toString(),
                "message", "Guide with this type already exists for the quote"
        );
        return error("guide_already_exists", HttpStatus.CONFLICT, details);
    }

    @ExceptionHandler(com.app.redcarga.deals.domain.exceptions.QuoteNotAcceptedException.class)
    public ResponseEntity<Object> onQuoteNotAccepted(com.app.redcarga.deals.domain.exceptions.QuoteNotAcceptedException ex) {
        var details = Map.of(
                "quoteId", ex.getQuoteId(),
                "message", "Quote must be in ACEPTADA state to create guides"
        );
        return error("quote_not_accepted", HttpStatus.BAD_REQUEST, details);
    }

    private static final Logger log = LoggerFactory.getLogger(DealsExceptionHandler.class);

    /** DEV helper: devuelve message y loggea stacktrace para 500 */ 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> onUnhandled(Exception ex) {
        log.error("Unhandled exception in controller", ex);
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}