package com.app.redcarga.fleet.interfaces.rest.requests;

public record UpdateDriverRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String licenseNumber,
        Boolean active
) {}


