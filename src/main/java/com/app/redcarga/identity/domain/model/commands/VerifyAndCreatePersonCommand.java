package com.app.redcarga.identity.domain.model.commands;

import java.time.LocalDate;

public record VerifyAndCreatePersonCommand(
        int accountId,
        String fullName,
        String docTypeCode,   // "DNI" | "CE" | "PAS"
        String docNumber,
        LocalDate birthDate,
        String phone,
        String ruc
) {}
