package com.app.redcarga.deals.domain.services;

public interface ChecklistItemCommandService {
    /**
     * Marks a checklist item as DONE for the quote's checklist instance.
     * - Validates quote exists and is in ACEPTADA state
     * - Validates checklist instance exists
     * - Optionally checks dependencies (via ChecklistDependencyService)
     * - Throws ChecklistDependencyException if dependencies missing
     * - Throws DomainException for other not-found / state errors
     */
    void markItemDone(Integer quoteId, String itemCode, boolean checkDependencies, Integer actorAccountId);
}
