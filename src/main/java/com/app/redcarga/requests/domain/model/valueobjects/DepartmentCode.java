package com.app.redcarga.requests.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class DepartmentCode {
    @Column(name = "department_code", length = 2, nullable = false)
    private String value;

    protected DepartmentCode() {}
    private DepartmentCode(String v) { this.value = v; }

    public static DepartmentCode of(String raw) {
        if (raw == null || (raw = raw.trim()).length() != 2 || !raw.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("department_code_invalid");
        return new DepartmentCode(raw);
    }

    @Override public String toString() { return value; }
}
