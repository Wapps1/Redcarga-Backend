package com.app.redcarga.fleet.interfaces.rest.requests;

public record UpdateDriverRequest(
        String licenseNumber,
        Boolean active
) {}


