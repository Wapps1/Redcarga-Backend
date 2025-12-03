package com.app.redcarga.deals.domain.services;

import com.app.redcarga.deals.interfaces.rest.responses.ChecklistItemResponse;

import java.util.List;

public interface ChecklistQueryService {
    List<ChecklistItemResponse> getChecklistItemsByQuoteId(Integer quoteId);
}
