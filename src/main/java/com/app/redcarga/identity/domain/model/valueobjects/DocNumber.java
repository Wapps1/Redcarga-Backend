package com.app.redcarga.identity.domain.model.valueobjects;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class DocNumber {
    private final String value;

    private DocNumber(String value) { this.value = value; }

    public static DocNumber of(DocTypeCode type, String raw) {
        if (raw == null) throw new IllegalArgumentException("docNumber required");
        String v = raw.trim();

        switch (type) {
            case DNI -> {
                if (!v.matches("\\d{8}"))
                    throw new IllegalArgumentException("DNI must be exactly 8 digits");
            }
            case CE, PAS -> {
                if (!v.matches("[A-Za-z0-9]{8,12}"))
                    throw new IllegalArgumentException("CE/PAS must be alphanumeric 8..12");
            }
        }
        return new DocNumber(v);
    }

    @Override public String toString() { return value; }
}
