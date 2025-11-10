package com.app.redcarga.deals.interfaces.rest.requests;

import jakarta.validation.constraints.Size;

/** Request body to propose an acceptance for a quote. Actor comes from the JWT principal. */
public record ProposeAcceptanceRequest(
        @Size(max = 64, message = "idempotency_key_too_long") String idempotencyKey,
        @Size(max = 2000, message = "acceptance_note_too_long") String note
) {}
