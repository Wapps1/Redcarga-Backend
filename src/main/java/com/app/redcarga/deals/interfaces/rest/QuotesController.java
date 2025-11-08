package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import com.app.redcarga.deals.interfaces.rest.requests.CreateQuoteRequest;
import com.app.redcarga.deals.interfaces.rest.responses.CreateQuoteResponse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deals/quotes")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class QuotesController {

    private final QuoteCommandService quoteCommandService;
    private final QuoteQueryService quoteQueryService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<CreateQuoteResponse> create(@Valid @RequestBody CreateQuoteRequest request,
                                                      JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        Integer id = quoteCommandService.create(request.toCommand(), accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateQuoteResponse(id));
    }

    // GET de refresh básico (listar por requestId y opcional stateCode)
    // Ej: GET /api/deals/quotes?requestId=123&state=PENDIENTE
    // Devuelve entidades por ahora; luego podemos mapear a DTO de view si prefieres.
    @GetMapping
    public ResponseEntity<?> listByRequest(@RequestParam("requestId") Integer requestId,
                                           @RequestParam(value = "state", required = false) String stateCode) {
        return ResponseEntity.ok(quoteQueryService.listByRequestIdAndState(requestId, stateCode));
    }

    // PATCH item quantity
    @PatchMapping("/{quoteId}/items")
    public ResponseEntity<Void> updateItemQty(@PathVariable Integer quoteId,
                                              @RequestBody @Valid UpdateItemQtyRequest body,
                                              JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        quoteCommandService.updateItemQty(quoteId, body.requestItemId(), body.qty(), accountId);
        return ResponseEntity.noContent().build();
    }

    // DELETE item
    @DeleteMapping("/{quoteId}/items/{requestItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Integer quoteId,
                                           @PathVariable Integer requestItemId,
                                           JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        quoteCommandService.removeItem(quoteId, requestItemId, accountId);
        return ResponseEntity.noContent().build();
    }

    // Simple inline request record for PATCH quantity
    public record UpdateItemQtyRequest(
            @NotNull Integer requestItemId,
            @NotNull @DecimalMin(value = "0.0001") java.math.BigDecimal qty
    ) {}
}

