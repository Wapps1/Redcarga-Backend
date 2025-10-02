package com.app.redcarga.shared.domain.model.valueobjects;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern P = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    public Email {
        if (value == null || value.isBlank() || !P.matcher(value).matches())
            throw new IllegalArgumentException("Invalid email");
    }
    @Override public String toString() { return value; }
}
