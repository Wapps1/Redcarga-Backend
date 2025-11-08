package com.app.redcarga.deals.domain.model.entities;

import com.app.redcarga.deals.domain.model.aggregates.Quote;


import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "deals", name = "quote_item",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"quote_id","request_item_id"})})
@AttributeOverride(name = "id", column = @Column(name = "quote_item_id"))
public class QuoteItem extends AuditableAbstractAggregateRoot<QuoteItem> {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(name = "request_item_id", nullable = false)
    private Integer requestItemId;

    @Column(name = "qty", nullable = false, precision = 18, scale = 3)
    private BigDecimal qty;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    public static QuoteItem createNew(Quote quote, Integer requestItemId, BigDecimal qty) {
        if (quote == null) throw new DomainException("quote_required");
        if (requestItemId == null || requestItemId <= 0) throw new DomainException("requestItemId_invalid");
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) throw new DomainException("qty_invalid");
        QuoteItem qi = new QuoteItem();
        qi.quote = quote;
        qi.requestItemId = requestItemId;
        qi.qty = qty;
        return qi;
    }

    public void updateQty(BigDecimal newQty) {
        if (newQty == null || newQty.compareTo(BigDecimal.ZERO) <= 0) throw new DomainException("qty_invalid");
        this.qty = newQty;
    }
}
