package com.app.redcarga.iam.interfaces.rest.responses;

public record ErrorResponse(String error) {
    public static ErrorResponse of(String code) { return new ErrorResponse(code); }
}
