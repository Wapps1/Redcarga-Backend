package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import com.app.redcarga.deals.interfaces.rest.requests.CreateQuoteRequest;
import com.app.redcarga.deals.interfaces.rest.responses.CreateQuoteResponse;
import com.app.redcarga.deals.interfaces.rest.responses.QuoteDetailResponse;
import com.app.redcarga.deals.interfaces.rest.responses.QuoteResponsesMapper;
import com.app.redcarga.deals.interfaces.rest.responses.QuoteGeneralSummaryResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // GET por requestId (opcional stateCode)
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> listByRequest(@RequestParam("requestId") Integer requestId,
                                           @RequestParam(value = "state", required = false) String stateCode) {
        var quotes = quoteQueryService.listByRequestIdAndState(requestId, stateCode);
        var body = quotes.stream().map(QuoteResponsesMapper::toGeneralSummary).toList();
        return ResponseEntity.ok(body);
    }

    // NUEVO: listado general (sin items) filtrando por company_id y state
    // Si state=TRATO, incluye también EN_ESPERA
    // GET /api/deals/quotes/general?company_id=10&state=TRATO
    @GetMapping("/general")
    public ResponseEntity<List<QuoteGeneralSummaryResponse>> listGeneral(
            @RequestParam("company_id") Integer companyId,
            @RequestParam(value = "state", required = false) String stateCode) {

        var quotes = quoteQueryService.listByCompanyIdAndState(companyId, stateCode);
        var body = quotes.stream().map(QuoteResponsesMapper::toGeneralSummary).toList();
        return ResponseEntity.ok(body);
    }

    // NUEVO: detalle de una quote con sus items
    // GET /api/deals/quotes/{quoteId}/detail
    @GetMapping("/{quoteId}/detail")
    public ResponseEntity<QuoteDetailResponse> getDetail(@PathVariable Integer quoteId) {
        var quoteOpt = quoteQueryService.getById(quoteId);
        if (quoteOpt.isEmpty()) return ResponseEntity.notFound().build();
        var items = quoteQueryService.listItemsByQuoteId(quoteId);
        var body = QuoteResponsesMapper.toDetail(quoteOpt.get(), items);
        return ResponseEntity.ok(body);
    }

    // PATCH item quantity
    @PatchMapping("/{quoteId}/items")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Void> updateItemQty(@PathVariable Integer quoteId,
                                              @RequestBody @Valid UpdateItemQtyRequest body,
                                              JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        quoteCommandService.updateItemQty(quoteId, body.requestItemId(), body.qty(), accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quoteId}:start-negotiation")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> startNegotiation(@PathVariable Integer quoteId,
                                                 @RequestHeader(value = "If-Match") String ifMatchHeader,
                                                 @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                 JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        Integer ifMatchVersion;
        try {
            ifMatchVersion = Integer.valueOf(ifMatchHeader.replace("\"", "").trim());
        } catch (Exception ex) {
            throw new com.app.redcarga.shared.domain.exceptions.DomainException("if_match_invalid");
        }
        quoteCommandService.startNegotiation(quoteId, accountId, ifMatchVersion, idempotencyKey);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{quoteId}:reject")
    public ResponseEntity<Void> rejectQuote(@PathVariable Integer quoteId,
                                            JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        quoteCommandService.rejectQuote(quoteId, accountId);
        return ResponseEntity.noContent().build();
    }

    // Inline request para PATCH quantity
    public record UpdateItemQtyRequest(
            @NotNull Integer requestItemId,
            @NotNull @DecimalMin(value = "0.0001") java.math.BigDecimal qty
    ) {}
}

