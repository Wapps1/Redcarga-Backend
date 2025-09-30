package com.app.redcarga.identity.domain.model.valueobjects;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class FullName {
    private final String value;

    public FullName(String raw) {
        if (raw == null) throw new IllegalArgumentException("fullName required");
        String v = raw.trim().replaceAll("\\s+", " ");
        if (v.length() < 2 || v.length() > 150) {
            throw new IllegalArgumentException("fullName length must be 2..150");
        }
        this.value = v;
    }

    @Override public String toString() { return value; }
}
