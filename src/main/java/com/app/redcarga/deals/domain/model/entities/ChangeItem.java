package com.app.redcarga.deals.domain.model.entities;

import com.app.redcarga.shared.domain.exceptions.DomainException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(schema = "deals", name = "change_item")
public class ChangeItem {

    @Id
    @Column(name = "change_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer changeItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_id", nullable = false)
    private Change change;

    @Column(name = "field_code", length = 32, nullable = false)
    private String fieldCode; // PRICE_TOTAL|QTY|ITEM_ADD|ITEM_REMOVE

    @Column(name = "target_quote_item_id")
    private Integer targetQuoteItemId;

    @Column(name = "target_request_item_id")
    private Integer targetRequestItemId;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    public static ChangeItem ofPriceTotal(String newTotal) {
        ChangeItem i = new ChangeItem();
        i.fieldCode = "PRICE_TOTAL";
        i.newValue = newTotal;
        return i;
    }

    public static ChangeItem ofQty(Integer targetQuoteItemId, String oldValue, String newValue) {
        if (targetQuoteItemId == null) throw new DomainException("target_quote_item_required");
        ChangeItem i = new ChangeItem();
        i.fieldCode = "QTY";
        i.targetQuoteItemId = targetQuoteItemId;
        i.oldValue = oldValue;
        i.newValue = newValue;
        return i;
    }

    public static ChangeItem ofItemAdd(Integer targetRequestItemId, String newValue) {
        if (targetRequestItemId == null) throw new DomainException("target_request_item_required");
        ChangeItem i = new ChangeItem();
        i.fieldCode = "ITEM_ADD";
        i.targetRequestItemId = targetRequestItemId;
        i.newValue = newValue;
        return i;
    }

    public static ChangeItem ofItemRemove(Integer targetQuoteItemId) {
        if (targetQuoteItemId == null) throw new DomainException("target_quote_item_required");
        ChangeItem i = new ChangeItem();
        i.fieldCode = "ITEM_REMOVE";
        i.targetQuoteItemId = targetQuoteItemId;
        return i;
    }

    /* package */ void setChange(Change change) {
        this.change = change;
    }
}
