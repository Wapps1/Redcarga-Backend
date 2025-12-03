package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.ChecklistQueryService;
import com.app.redcarga.deals.interfaces.rest.responses.ChecklistItemResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals/checklists")
@SecurityRequirement(name = "iam")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistQueryService checklistQueryService;

    @GetMapping("/by-quote/{quoteId}/items")
    public ResponseEntity<List<ChecklistItemResponse>> getItemsByQuote(@PathVariable Integer quoteId) {
        List<ChecklistItemResponse> items = checklistQueryService.getChecklistItemsByQuoteId(quoteId);
        return ResponseEntity.ok(items);
    }
}
