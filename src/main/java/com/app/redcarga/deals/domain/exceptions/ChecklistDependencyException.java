package com.app.redcarga.deals.domain.exceptions;

import java.util.List;

public class ChecklistDependencyException extends RuntimeException {
    private final List<String> missing;
    private final String code;

    public ChecklistDependencyException(String code, List<String> missing) {
        super(code);
        this.missing = missing;
        this.code = code;
    }

    public List<String> getMissing() { return missing; }
    public String getCode() { return code; }
}
