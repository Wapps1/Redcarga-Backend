package com.app.redcarga.requests.domain.model.aggregates;

import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.requests.domain.model.entities.RequestItem;
import com.app.redcarga.requests.domain.model.valueobjects.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "requests", name = "requests",
        indexes = {
                @Index(name="ix_requests_status_createdat", columnList = "status_id, created_at DESC"),
                @Index(name="ix_requests_requester_createdat", columnList = "requester_account_id, created_at DESC"),
                @Index(name="ix_requests_createdat", columnList = "created_at DESC")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id",         column = @Column(name = "request_id")),
        @AttributeOverride(name = "createdAt",  column = @Column(name = "created_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt",  column = @Column(name = "updated_at", nullable = false))
})
public class Request extends AuditableAbstractAggregateRoot<Request> {

    /** Identidad del solicitante (IAM) + snapshot para UI */
    @Column(name = "requester_account_id", nullable = false)
    private Integer requesterAccountId;

    @Column(name = "requester_name_snapshot", length = 200)
    private String requesterNameSnapshot;

    /** Estado persistido como FK a requests.request_statuses (ID) */
    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    /** Cierre (cuando aplique) */
    @Column(name = "closed_at")
    private Date closedAt;

    // -------- Origen
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "departmentCode.value", column = @Column(name = "origin_department_code", nullable = false, length = 2)),
            @AttributeOverride(name = "departmentName",       column = @Column(name = "origin_department_name", nullable = false, length = 100)),
            @AttributeOverride(name = "provinceCode.value",   column = @Column(name = "origin_province_code", length = 4)),
            @AttributeOverride(name = "provinceName",         column = @Column(name = "origin_province_name", length = 100)),
            @AttributeOverride(name = "districtText.value",   column = @Column(name = "origin_district_text", length = 150))
    })
    private UbigeoSnapshot origin;

    // -------- Destino
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "departmentCode.value", column = @Column(name = "dest_department_code", nullable = false, length = 2)),
            @AttributeOverride(name = "departmentName",       column = @Column(name = "dest_department_name", nullable = false, length = 100)),
            @AttributeOverride(name = "provinceCode.value",   column = @Column(name = "dest_province_code", length = 4)),
            @AttributeOverride(name = "provinceName",         column = @Column(name = "dest_province_name", length = 100)),
            @AttributeOverride(name = "districtText.value",   column = @Column(name = "dest_district_text", length = 150))
    })
    private UbigeoSnapshot destination;

    // -------- Agregados (no calculados aquí; se persisten tal cual)
    @Column(name = "items_count")
    private Integer itemsCount;

    @Column(name = "total_weight_kg", precision = 12, scale = 3)
    private BigDecimal totalWeightKg;

    // -------- Items (entidades internas del aggregate)
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private final List<RequestItem> items = new ArrayList<>();

    /* ====== Factories ====== */

    public static Request create(Integer requesterAccountId,
                                 String requesterNameSnapshot,
                                 UbigeoSnapshot origin,
                                 UbigeoSnapshot destination,
                                 Integer itemsCount,
                                 BigDecimal totalWeightKg,
                                 RequestStatusCode initialStatus) {
        Request r = new Request();
        r.requesterAccountId   = requirePositive(requesterAccountId, "requester_account_id_invalid");
        r.requesterNameSnapshot= sanitizeOptional(requesterNameSnapshot, 1, 200);
        r.origin               = Objects.requireNonNull(origin, "origin_required");
        r.destination          = Objects.requireNonNull(destination, "destination_required");
        r.itemsCount           = itemsCount != null && itemsCount >= 0 ? itemsCount : null;
        r.totalWeightKg        = normalizeDecimal(totalWeightKg, 12, 3, "total_weight_invalid");
        r.statusId = RequestStatusCode.require(initialStatus).getId();
        return r;
    }

    /* ====== Comportamiento ====== */

    public void addItem(RequestItem item) {
        Objects.requireNonNull(item, "item_required");
        item.attachTo(this, nextPosition());
        this.items.add(item);
    }

    public void replaceItems(List<RequestItem> newItems) {
        this.items.clear();
        int pos = 1;
        for (RequestItem i : newItems) {
            i.attachTo(this, pos++);
            this.items.add(i);
        }
        this.itemsCount = this.items.size();
    }

    public void close(CloseReasonCode reason) {
        RequestStatusCode current = currentStatus();
        if (!current.canTransitionTo(RequestStatusCode.CLOSED)) {
            throw new IllegalStateException("request_close_invalid_transition");
        }
        this.statusId = RequestStatusCode.CLOSED.getId();
        this.closedAt = new Date();
        // El historial se persiste fuera (application) usando reason.
    }

    public void cancel(CloseReasonCode reason) {
        RequestStatusCode current = currentStatus();
        if (!current.canTransitionTo(RequestStatusCode.CANCELLED)) {
            throw new IllegalStateException("request_cancel_invalid_transition");
        }
        this.statusId = RequestStatusCode.CANCELLED.getId();
        this.closedAt = new Date();
    }

    public void expire() {
        RequestStatusCode current = currentStatus();
        if (!current.canTransitionTo(RequestStatusCode.EXPIRED)) {
            throw new IllegalStateException("request_expire_invalid_transition");
        }
        this.statusId = RequestStatusCode.EXPIRED.getId();
        this.closedAt = new Date();
    }

    public RequestStatusCode currentStatus() {
        return RequestStatusCode.fromId(this.statusId);
    }

    /* ====== Helpers ====== */

    private int nextPosition() { return this.items.size() + 1; }

    private static Integer requirePositive(Integer v, String code) {
        if (v == null || v <= 0) throw new IllegalArgumentException(code);
        return v;
    }

    private static String sanitizeOptional(String raw, int min, int max) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isEmpty()) return null;
        if (t.length() > max) throw new IllegalArgumentException("snapshot_name_too_long");
        return t;
    }

    private static BigDecimal normalizeDecimal(BigDecimal v, int precision, int scale, String code) {
        if (v == null) return null;
        try {
            var s = v.setScale(scale);
            if (s.precision() > precision) throw new IllegalArgumentException(code);
            return s;
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(code);
        }
    }
}
