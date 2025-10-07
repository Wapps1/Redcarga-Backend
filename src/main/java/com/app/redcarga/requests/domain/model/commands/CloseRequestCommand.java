package com.app.redcarga.requests.domain.model.commands;

import com.app.redcarga.requests.domain.model.valueobjects.CloseReasonCode;

public record CloseRequestCommand(
        Integer requestId,
        CloseReasonCode reason
) {}
