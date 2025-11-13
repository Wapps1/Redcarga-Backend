package com.app.redcarga.deals.domain.model.entities;

import com.app.redcarga.deals.domain.model.aggregates.Quote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "deals", name = "change")
public class Change {

    @Id
    @Column(name = "change_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer changeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "kind_code", length = 16, nullable = false)
    private String kindCode; // LIBRE | PROPUESTA

    @Column(name = "status_code", length = 16, nullable = false)
    private String statusCode; // APLICADO | PENDIENTE | RECHAZADO

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "change", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeItem> items = new ArrayList<>();

    public static Change createApplied(Quote quote, String kindCode, Integer createdBy) {
        if (quote == null) throw new com.app.redcarga.shared.domain.exceptions.DomainException("quote_required_for_change");
        if (kindCode == null) throw new com.app.redcarga.shared.domain.exceptions.DomainException("change_kind_required");
        if (createdBy == null) throw new com.app.redcarga.shared.domain.exceptions.DomainException("created_by_required");
        Change c = new Change();
        c.quote = quote;
        c.kindCode = kindCode;
        c.statusCode = "APLICADO";
        c.createdBy = createdBy;
        c.createdAt = OffsetDateTime.now();
        return c;
    }

    public void addItem(ChangeItem item) {
        if (item == null) return;
        item.setChange(this);
        this.items.add(item);
    }

    public void markPending() {
        this.statusCode = "PENDIENTE";
    }

    public void markApplied() {
        this.statusCode = "APLICADO";
    }

    public void markRejected() {
        this.statusCode = "RECHAZADO";
    }
}
