package com.app.redcarga.deals.interfaces.rest.requests;

import com.app.redcarga.deals.domain.model.entities.ChangeItem;

import java.util.List;
import java.util.stream.Collectors;

public class ChangeRequest {
    public List<ChangeItemDto> items;

    public List<ChangeItem> toDomainItems() {
        if (items == null) return List.of();
        return items.stream().map(ChangeItemDto::toDomain).collect(Collectors.toList());
    }
}
