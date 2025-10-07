package com.app.redcarga.requests.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class ItemName {
    @Column(name = "item_name", length = 160, nullable = false)
    private String value;

    protected ItemName() {}
    private ItemName(String v) { this.value = v; }

    public static ItemName of(String raw) {
        if (raw == null) throw new IllegalArgumentException("item_name_required");
        String t = raw.trim();
        if (t.isEmpty() || t.length() > 160)
            throw new IllegalArgumentException("item_name_invalid");
        return new ItemName(t);
    }

    @Override public String toString() { return value; }
}
