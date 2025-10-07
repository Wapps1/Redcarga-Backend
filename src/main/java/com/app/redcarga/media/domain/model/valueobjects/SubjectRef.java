package com.app.redcarga.media.domain.model.valueobjects;

import java.util.Objects;

/** Referencia al “sujeto” dueño del upload (no conoce Cloudinary). */
public final class SubjectRef {
    private final MediaSubjectType type;
    private final String key; // p.ej. "request:REQ123:item:ITEM9" o "provider:42:logo"

    private SubjectRef(MediaSubjectType type, String key) {
        this.type = Objects.requireNonNull(type, "type");
        this.key = requireKey(key);
    }

    public static SubjectRef of(MediaSubjectType type, String key) {
        return new SubjectRef(type, key);
    }

    public MediaSubjectType type() { return type; }
    public String key() { return key; }

    private static String requireKey(String raw) {
        if (raw == null || (raw = raw.trim()).isEmpty() || raw.length() > 200)
            throw new IllegalArgumentException("subject_key_invalid");
        return raw;
    }

    @Override public String toString() { return type + ":" + key; }
    @Override public boolean equals(Object o){ return o instanceof SubjectRef r && type==r.type && key.equals(r.key); }
    @Override public int hashCode(){ return Objects.hash(type, key); }
}
