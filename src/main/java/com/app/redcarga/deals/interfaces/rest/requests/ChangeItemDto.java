package com.app.redcarga.deals.interfaces.rest.requests;

import com.app.redcarga.deals.domain.model.entities.ChangeItem;

public class ChangeItemDto {
    public String fieldCode; // PRICE_TOTAL|QTY|ITEM_ADD|ITEM_REMOVE
    public Integer targetQuoteItemId;
    public Integer targetRequestItemId;
    public String oldValue;
    public String newValue;

    public ChangeItem toDomain() {
        if (fieldCode == null) throw new com.app.redcarga.shared.domain.exceptions.DomainException("change_item_field_required");
        switch (fieldCode) {
            case "PRICE_TOTAL":
                return ChangeItem.ofPriceTotal(newValue);
            case "QTY":
                return ChangeItem.ofQty(targetQuoteItemId, oldValue, newValue);
            case "ITEM_ADD":
                return ChangeItem.ofItemAdd(targetRequestItemId, newValue);
            case "ITEM_REMOVE":
                return ChangeItem.ofItemRemove(targetQuoteItemId);
            default:
                throw new com.app.redcarga.shared.domain.exceptions.DomainException("change_item_field_unknown");
        }
    }
}
