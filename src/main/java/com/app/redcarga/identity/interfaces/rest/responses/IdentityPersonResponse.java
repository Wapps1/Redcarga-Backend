package com.app.redcarga.identity.interfaces.rest.responses;

import java.time.LocalDate;

public record IdentityPersonResponse(
        Integer id,
        Integer accountId,
        String fullName,
        LocalDate birthDate,
        Integer docTypeId,
        String docNumber,
        String phone,
        String ruc
) {}
