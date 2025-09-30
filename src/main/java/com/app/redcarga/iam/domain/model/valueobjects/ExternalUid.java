package com.app.redcarga.iam.domain.model.valueobjects;

public record ExternalUid(String value) {
    public ExternalUid {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("External UID required");
    }
    @Override public String toString() { return value; }
}
