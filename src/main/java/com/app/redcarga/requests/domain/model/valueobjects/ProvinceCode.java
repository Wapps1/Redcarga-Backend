package com.app.redcarga.requests.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class ProvinceCode {
    @Column(name = "province_code", length = 4)
    private String value;

    protected ProvinceCode() {}
    private ProvinceCode(String v) { this.value = v; }

    public static ProvinceCode ofNullable(String raw) {
        if (raw == null || raw.isBlank()) return new ProvinceCode(null);
        String t = raw.trim();
        if (t.length() != 4 || !t.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("province_code_invalid");
        return new ProvinceCode(t);
    }

    @Override public String toString() { return value; }
}
