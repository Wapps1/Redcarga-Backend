package com.app.redcarga.requests.domain.model.entities;

import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.model.valueobjects.ItemName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "requests", name = "request_items",
        indexes = @Index(name="ix_request_items_request_pos", columnList = "request_id, position"))
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "item_name", nullable = false, length = 160))
    private ItemName itemName;

    @Column(name = "height_cm", precision = 8, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "width_cm", precision = 8, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "length_cm", precision = 8, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "weight_kg", precision = 10, scale = 3)
    private BigDecimal weightKg;

    @Column(name = "total_weight_kg", precision = 10, scale = 3)
    private BigDecimal totalWeightKg;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "fragile", nullable = false)
    private boolean fragile;

    @Column(name = "notes", length = 400)
    private String notes;

    @Column(name = "position", nullable = false)
    private Integer position;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("imagePosition ASC")
    private final List<RequestItemImage> images = new ArrayList<>();

    /* Factory */
    public static RequestItem of(String itemName,
                                 BigDecimal heightCm,
                                 BigDecimal widthCm,
                                 BigDecimal lengthCm,
                                 BigDecimal weightKg,
                                 BigDecimal totalWeightKg,
                                 Integer quantity,
                                 boolean fragile,
                                 String notes) {
        RequestItem it = new RequestItem();
        it.itemName      = ItemName.of(itemName);
        it.heightCm      = normalize(heightCm, 8, 2, "height_invalid");
        it.widthCm       = normalize(widthCm, 8, 2, "width_invalid");
        it.lengthCm      = normalize(lengthCm, 8, 2, "length_invalid");
        it.weightKg      = normalize(weightKg, 10, 3, "weight_invalid");
        it.totalWeightKg = normalize(totalWeightKg, 10, 3, "total_weight_invalid");
        it.quantity      = requirePositive(quantity, "quantity_invalid");
        it.fragile       = fragile;
        it.notes         = sanitizeOptional(notes, 1, 400);
        return it;
    }

    /* Behavior */
    public void attachTo(Request request, int position) {
        this.request  = Objects.requireNonNull(request, "request_required");
        this.position = position;
    }

    public void addImage(RequestItemImage img) {
        Objects.requireNonNull(img, "image_required");
        img.attachTo(this, nextImagePos());
        this.images.add(img);
    }

    public void replaceImages(List<RequestItemImage> newImages) {
        this.images.clear();
        int pos = 1;
        for (RequestItemImage im : newImages) {
            im.attachTo(this, pos++);
            this.images.add(im);
        }
    }

    private int nextImagePos() { return this.images.size() + 1; }

    /* Helpers */
    private static Integer requirePositive(Integer v, String code) {
        if (v == null || v <= 0) throw new IllegalArgumentException(code);
        return v;
    }

    private static BigDecimal normalize(BigDecimal v, int precision, int scale, String code) {
        if (v == null) return null;
        try {
            var s = v.setScale(scale);
            if (s.precision() > precision) throw new IllegalArgumentException(code);
            return s;
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(code);
        }
    }

    private static String sanitizeOptional(String raw, int min, int max) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isEmpty()) return null;
        if (t.length() > max) throw new IllegalArgumentException("notes_too_long");
        return t;
    }
}
