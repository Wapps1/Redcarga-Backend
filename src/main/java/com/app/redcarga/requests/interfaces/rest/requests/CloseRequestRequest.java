package com.app.redcarga.requests.interfaces.rest.requests;

import jakarta.validation.constraints.NotBlank;

public record CloseRequestRequest(@NotBlank String reason) {}
