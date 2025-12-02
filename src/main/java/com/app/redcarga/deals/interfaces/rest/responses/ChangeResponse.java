package com.app.redcarga.deals.interfaces.rest.responses;

import com.app.redcarga.deals.domain.model.entities.Change;
import com.app.redcarga.deals.domain.model.entities.ChangeItem;

import java.time.OffsetDateTime;
import java.util.List;

public record ChangeResponse(
        Integer changeId,
        Integer quoteId,
        String kindCode,
        String statusCode,
        Integer createdBy,
        OffsetDateTime createdAt,
        List<ChangeItemResponse> items
) {
    public static ChangeResponse from(Change change, List<ChangeItem> items) {
        return new ChangeResponse(
                change.getChangeId(),
                change.getQuote().getId(), // solo el ID, no carga lazy
                change.getKindCode(),
                change.getStatusCode(),
                change.getCreatedBy(),
                change.getCreatedAt(),
                items.stream().map(ChangeItemResponse::from).toList()
        );
    }
}


record ChangeItemResponse(
        Integer changeItemId,
        String fieldCode,
        String oldValue,
        String newValue,
        Integer targetQuoteItemId,
        Integer targetRequestItemId
) {
    public static ChangeItemResponse from(ChangeItem item) {
        return new ChangeItemResponse(
                item.getChangeItemId(),
                item.getFieldCode(),
                item.getOldValue(),
                item.getNewValue(),
                item.getTargetQuoteItemId(),
                item.getTargetRequestItemId()
        );
    }
}