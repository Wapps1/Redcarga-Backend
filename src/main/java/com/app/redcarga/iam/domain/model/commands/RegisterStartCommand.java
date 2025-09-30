package com.app.redcarga.iam.domain.model.commands;

public record RegisterStartCommand(String email, String username, char[] rawPassword,
                                   String roleCode, String platform, String idempotencyKey) {
    public RegisterStartCommand {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username required");
        if (rawPassword == null || rawPassword.length < 8) throw new IllegalArgumentException("weak password");
        if (roleCode == null || roleCode.isBlank()) throw new IllegalArgumentException("roleCode required");
        if (platform == null || platform.isBlank()) throw new IllegalArgumentException("platform required");
    }
}
