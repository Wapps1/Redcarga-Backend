package com.app.redcarga.identity.domain.model.valueobjects;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class ProviderCode {
    private final String value;

    public ProviderCode(String raw) {
        if (raw == null) throw new IllegalArgumentException("provider_code required");
        String v = raw.trim();
        if (v.isEmpty() || v.length() > 40) throw new IllegalArgumentException("provider_code length 1..40");
        this.value = v;
    }

    @Override public String toString() { return value; }
}
