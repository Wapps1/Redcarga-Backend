package com.app.redcarga.iam.domain.model.commands;

public record MarkEmailVerifiedCommand(Integer accountId) {
    public MarkEmailVerifiedCommand {
        if (accountId == null || accountId <= 0) throw new IllegalArgumentException("invalid accountId");
    }
}