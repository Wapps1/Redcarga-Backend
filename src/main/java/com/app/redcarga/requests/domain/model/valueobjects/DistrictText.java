package com.app.redcarga.requests.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class DistrictText {
    @Column(name = "district_text", length = 150)
    private String value;

    protected DistrictText() {}
    private DistrictText(String v) { this.value = v; }

    public static DistrictText ofNullable(String raw) {
        if (raw == null) return new DistrictText(null);
        String t = raw.trim();
        if (t.isEmpty()) return new DistrictText(null);
        if (t.length() > 150) throw new IllegalArgumentException("district_text_too_long");
        return new DistrictText(t);
    }

    @Override public String toString() { return value; }
}
