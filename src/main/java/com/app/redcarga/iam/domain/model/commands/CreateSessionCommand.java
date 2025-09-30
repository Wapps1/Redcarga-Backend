package com.app.redcarga.iam.domain.model.commands;

public record CreateSessionCommand(Integer accountId, String platform, String ipAddress, long ttlSeconds) {
    public CreateSessionCommand {
        if (accountId == null || accountId <= 0) throw new IllegalArgumentException("invalid accountId");
        if (platform == null || platform.isBlank()) throw new IllegalArgumentException("platform required");
    }
}
