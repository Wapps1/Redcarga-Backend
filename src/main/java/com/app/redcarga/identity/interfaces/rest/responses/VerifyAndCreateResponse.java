package com.app.redcarga.identity.interfaces.rest.responses;

public record VerifyAndCreateResponse(
        boolean passed,
        int personId
) {}
