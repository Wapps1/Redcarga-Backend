package com.app.redcarga.iam.domain.model.commands;

public record LogoutCommand(Integer sessionId) {
    public LogoutCommand {
        if (sessionId == null || sessionId <= 0) throw new IllegalArgumentException("invalid sessionId");
    }
}