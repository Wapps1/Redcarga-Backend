package com.app.redcarga.iam.domain.model.valueobjects;

public record Username(String value) {
    public Username {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Username required");
        if (value.length() < 3 || value.length() > 50) throw new IllegalArgumentException("Username length 3..50");
    }
    @Override public String toString() { return value; }
}
