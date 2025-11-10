package com.app.redcarga.deals.domain.model.aggregates;

import com.app.redcarga.deals.domain.model.entities.QuoteItem;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.app.redcarga.deals.domain.model.commands.CreateQuoteCommand;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.deals.domain.model.valueobjects.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "deals", name = "quote")
@AttributeOverride(name = "id", column = @Column(name = "quote_id"))
public class Quote extends AuditableAbstractAggregateRoot<Quote> {

    @Column(name = "request_id", nullable = false)
    private Integer requestId;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    // Align column name with DDL (created_by)
    @Column(name = "created_by", nullable = false)
    private Integer createdByAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", length = 3, nullable = false)
    private Currency currency = Currency.PEN;

    @Setter
    @Column(name = "state_code", length = 32, nullable = false)
    private String stateCode;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<QuoteItem> items = new java.util.ArrayList<>();

    // Factory
    public static Quote create(CreateQuoteCommand cmd, Integer creatorAccountId) {
    if (cmd == null) throw new DomainException("create_quote_command_required");
    if (creatorAccountId == null) throw new DomainException("creator_account_required");
    if (cmd.requestId() == null || cmd.requestId() <= 0) throw new DomainException("requestId_invalid");
    if (cmd.companyId() == null || cmd.companyId() <= 0) throw new DomainException("companyId_invalid");
    if (cmd.totalAmount() == null || cmd.totalAmount().compareTo(BigDecimal.ZERO) <= 0) throw new DomainException("totalAmount_invalid");

        Quote q = new Quote();
        q.requestId = cmd.requestId();
        q.companyId = cmd.companyId();
        q.createdByAccountId = creatorAccountId;
        q.totalAmount = cmd.totalAmount();
        q.currency = cmd.currency() == null ? Currency.PEN : cmd.currency();
        q.stateCode = "PENDIENTE";
        // build items if provided
        if (cmd.items() != null) {
            for (var it : cmd.items()) {
                q.addItem(it.requestItemId(), it.qty());
            }
        }
        return q;
    }

    // Aggregate operations for items
    public void addItem(Integer requestItemId, java.math.BigDecimal qty) {
        ensureEditable();
        if (requestItemId == null || requestItemId <= 0) throw new DomainException("requestItemId_invalid");
        if (qty == null || qty.compareTo(java.math.BigDecimal.ZERO) <= 0) throw new DomainException("qty_invalid");
        boolean exists = items.stream().anyMatch(i -> i.getRequestItemId().equals(requestItemId));
        if (exists) throw new DomainException("quote_item_duplicate");
        var item = com.app.redcarga.deals.domain.model.entities.QuoteItem.createNew(this, requestItemId, qty);
        this.items.add(item);
    }

    public void updateItemQty(Integer requestItemId, java.math.BigDecimal newQty) {
        ensureEditable();
        if (newQty == null || newQty.compareTo(java.math.BigDecimal.ZERO) <= 0) throw new DomainException("qty_invalid");
        var item = items.stream().filter(i -> i.getRequestItemId().equals(requestItemId)).findFirst()
                .orElseThrow(() -> new DomainException("quote_item_not_found"));
        item.updateQty(newQty);
    }

    public void updateTotalAmount(java.math.BigDecimal newTotal) {
        ensureEditable();
        if (newTotal == null || newTotal.compareTo(java.math.BigDecimal.ZERO) <= 0) throw new DomainException("totalAmount_invalid");
        this.totalAmount = newTotal;
    }

    public void removeItem(Integer requestItemId) {
        ensureEditable();
        var it = items.stream().filter(i -> i.getRequestItemId().equals(requestItemId)).findFirst()
                .orElseThrow(() -> new DomainException("quote_item_not_found"));
        items.remove(it);
    }

    private void ensureEditable() {
        if (this.stateCode == null) throw new DomainException("quote_state_not_editable");
        // Allowed: PENDIENTE, TRATO, EN_ESPERA
        String s = this.stateCode;
        if (!("PENDIENTE".equals(s) || "TRATO".equals(s) || "EN_ESPERA".equals(s))) {
            throw new DomainException("quote_state_not_editable");
        }
    }

    /** Transition PENDIENTE -> TRATO (start negotiation). Throws DomainException if invalid. */
    public void startNegotiation() {
        if (this.stateCode == null) throw new DomainException("quote_state_transition_invalid");
        if (!"PENDIENTE".equals(this.stateCode)) throw new DomainException("quote_state_transition_invalid");
        this.stateCode = "TRATO";
    }

}
