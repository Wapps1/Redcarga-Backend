package com.app.redcarga.requests.domain.model.aggregates;

import com.app.redcarga.requests.domain.model.entities.RequestItem;
import com.app.redcarga.requests.domain.model.valueobjects.RequestStatusCode;
import com.app.redcarga.requests.domain.model.valueobjects.UbigeoSnapshot;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
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
                @Index(name = "ix_requests_status_createdat", columnList = "status_id, created_at DESC"),
                @Index(name = "ix_requests_requester_createdat", columnList = "requester_account_id, created_at DESC"),
                @Index(name = "ix_requests_createdat", columnList = "created_at DESC")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id",        column = @Column(name = "request_id")),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", nullable = false))
})
public class Request extends AuditableAbstractAggregateRoot<Request> {

    // ---- Identidad del solicitante
    @Column(name = "requester_account_id", nullable = false)
    private Integer requesterAccountId;

    @Column(name = "requester_name_snapshot", length = 200)
    private String requesterNameSnapshot;

    // Nuevo campo: nombre de la solicitud (opcional)
    @Column(name = "request_name", length = 255)
    private String requestName;

    // ---- Estado (FK -> requests.request_statuses)
    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "closed_at")
    private Date closedAt;

    // ---- Origen
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "departmentCode.value", column = @Column(name = "origin_department_code", nullable = false, length = 2)),
            @AttributeOverride(name = "departmentName",       column = @Column(name = "origin_department_name",  nullable = false, length = 100)),
            @AttributeOverride(name = "provinceCode.value",   column = @Column(name = "origin_province_code",    length = 4)),
            @AttributeOverride(name = "provinceName",         column = @Column(name = "origin_province_name",    length = 100)),
            @AttributeOverride(name = "districtText.value",   column = @Column(name = "origin_district_text",    length = 150))
    })
    private UbigeoSnapshot origin;

    // ---- Destino
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "departmentCode.value", column = @Column(name = "dest_department_code", nullable = false, length = 2)),
            @AttributeOverride(name = "departmentName",       column = @Column(name = "dest_department_name",  nullable = false, length = 100)),
            @AttributeOverride(name = "provinceCode.value",   column = @Column(name = "dest_province_code",    length = 4)),
            @AttributeOverride(name = "provinceName",         column = @Column(name = "dest_province_name",    length = 100)),
            @AttributeOverride(name = "districtText.value",   column = @Column(name = "dest_district_text",    length = 150))
    })
    private UbigeoSnapshot destination;

    // ---- Snapshots para listados (los mantiene el aggregate)
    @Column(name = "items_count")
    private Integer itemsCount;

    @Column(name = "total_weight_kg", precision = 12, scale = 3)
    private BigDecimal totalWeightKg;

    // ---- Ítems
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private final List<RequestItem> items = new ArrayList<>();

    @Column(name = "payment_on_delivery", nullable = false)
    private boolean paymentOnDelivery = false;

    /* ======================== FACTORY ======================== */

    /** Crea una solicitud en estado OPEN con ≥1 ítem. */
    public static Request createOpen(
        Integer requesterAccountId,
        String requesterNameSnapshot,
        UbigeoSnapshot origin,
        UbigeoSnapshot destination,
        List<RequestItem> items,
        boolean paymentOnDelivery,
        String requestName
    ) {
        Request r = new Request();
        r.requesterAccountId     = requirePositive(requesterAccountId, "requester_account_id_invalid");
        r.requesterNameSnapshot  = sanitizeOptional(requesterNameSnapshot, 1, 200);
        r.origin                 = Objects.requireNonNull(origin, "origin_required");
        r.destination            = Objects.requireNonNull(destination, "destination_required");

        // Origen ≠ destino (compara por códigos)
        if (sameRoute(r.origin, r.destination)) throw new IllegalArgumentException("origin_equals_destination");

        if (items == null || items.isEmpty()) throw new IllegalArgumentException("request_requires_items");

        // Adjuntar ítems y calcular snapshots
        int pos = 1;
        BigDecimal total = BigDecimal.ZERO;
        for (RequestItem it : items) {
            it.attachTo(r, pos++);
            r.items.add(it);
            total = total.add(resolveItemTotalWeight(it));
        }
        r.itemsCount    = r.items.size();
        r.totalWeightKg = normalizeDecimal(total, 12, 3, "total_weight_invalid");

        // Estado inicial OPEN
        r.statusId = RequestStatusCode.OPEN.getId();

        r.paymentOnDelivery = paymentOnDelivery;

    // asignar requestName (solo sanitizar, puede ser null)
    r.requestName = sanitizeOptional(requestName, 1, 255);

        return r;
    }

    /* ===================== COMPORTAMIENTO ===================== */

    public void addItem(RequestItem item) {
        Objects.requireNonNull(item, "item_required");
        requireOpenState("request_add_item_only_when_open");

        item.attachTo(this, nextPosition());
        this.items.add(item);

        // mantener snapshots
        this.itemsCount = (this.itemsCount == null ? 0 : this.itemsCount) + 1;
        BigDecimal add = resolveItemTotalWeight(item);
        this.totalWeightKg = normalizeDecimal(
                (this.totalWeightKg == null ? BigDecimal.ZERO : this.totalWeightKg).add(add),
                12, 3, "total_weight_invalid"
        );
    }

    /** Reemplaza todos los ítems y recalcula snapshots. */
    public void replaceItems(List<RequestItem> newItems) {
        Objects.requireNonNull(newItems, "items_required");
        requireOpenState("request_replace_items_only_when_open");
        if (newItems.isEmpty()) throw new IllegalArgumentException("request_requires_items");

        this.items.clear();
        int pos = 1;
        BigDecimal total = BigDecimal.ZERO;
        for (RequestItem i : newItems) {
            i.attachTo(this, pos++);
            this.items.add(i);
            total = total.add(resolveItemTotalWeight(i));
        }
        this.itemsCount    = this.items.size();
        this.totalWeightKg = normalizeDecimal(total, 12, 3, "total_weight_invalid");
    }

    public void close(/* CloseReasonCode reason */) {
        RequestStatusCode current = currentStatus();
        if (!current.canTransitionTo(RequestStatusCode.CLOSED))
            throw new IllegalStateException("request_close_invalid_transition");
        this.statusId = RequestStatusCode.CLOSED.getId();
        this.closedAt = new Date();
    }

    public void cancel(/* CloseReasonCode reason */) {
        RequestStatusCode current = currentStatus();
        if (!current.canTransitionTo(RequestStatusCode.CANCELLED))
            throw new IllegalStateException("request_cancel_invalid_transition");
        this.statusId = RequestStatusCode.CANCELLED.getId();
        this.closedAt = new Date();
    }

    public void expire() {
        RequestStatusCode current = currentStatus();
        if (!current.canTransitionTo(RequestStatusCode.EXPIRED))
            throw new IllegalStateException("request_expire_invalid_transition");
        this.statusId = RequestStatusCode.EXPIRED.getId();
        this.closedAt = new Date();
    }

    public RequestStatusCode currentStatus() { return RequestStatusCode.fromId(this.statusId); }

    /* ======================== HELPERS ======================== */

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

    private static BigDecimal resolveItemTotalWeight(RequestItem it) {
        BigDecimal zero = new BigDecimal("0.000");
        if (it.getTotalWeightKg() != null) return it.getTotalWeightKg();
        if (it.getWeightKg() != null && it.getQuantity() != null) {
            return it.getWeightKg().multiply(new BigDecimal(it.getQuantity()));
        }
        return zero;
    }

    private static boolean sameRoute(UbigeoSnapshot a, UbigeoSnapshot b) {
        // Compara dept/prov por código; si alguno no tiene provincia, compara solo dept.
        String ad = safe(a.getDepartmentCode() != null ? a.getDepartmentCode().getValue() : null);
        String bd = safe(b.getDepartmentCode() != null ? b.getDepartmentCode().getValue() : null);
        String ap = safe(a.getProvinceCode()   != null ? a.getProvinceCode().getValue()   : null);
        String bp = safe(b.getProvinceCode()   != null ? b.getProvinceCode().getValue()   : null);
        if (!ad.equals(bd)) return false;
        // Si ambas provincias vienen, compáralas; si no, considera solo dept.
        if (!ap.isEmpty() || !bp.isEmpty()) return ad.equals(bd) && ap.equals(bp);
        return true; // mismo departamento sin provincia → igual
    }

    private static String safe(String v) { return v == null ? "" : v; }

    private void requireOpenState(String code) {
        if (currentStatus() != RequestStatusCode.OPEN) throw new IllegalStateException(code);
    }
}
