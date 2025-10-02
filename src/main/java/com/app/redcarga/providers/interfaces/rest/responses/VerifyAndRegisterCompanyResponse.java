package com.app.redcarga.providers.interfaces.rest.responses;

public record VerifyAndRegisterCompanyResponse(
        boolean success,
        Integer companyId
) {}
