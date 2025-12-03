package com.app.redcarga.tracking.interfaces.rest;

import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.tracking.interfaces.rest.responses.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = TrackingController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        String code = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if ("not_authorized_for_quote".equals(code)) status = HttpStatus.FORBIDDEN;
        else if ("location_not_found".equals(code)) status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("internal_error", ex.getMessage(), null));
    }
}
