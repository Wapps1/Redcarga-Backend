package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import com.app.redcarga.deals.interfaces.rest.requests.CreateQuoteRequest;
import com.app.redcarga.deals.interfaces.rest.responses.CreateQuoteResponse;
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
@PreAuthorize("hasRole('PROVIDER')")
public class QuotesController {

    private final QuoteCommandService quoteCommandService;
    private final QuoteQueryService quoteQueryService;

    @PostMapping
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listByRequest(@RequestParam("requestId") Integer requestId,
                                           @RequestParam(value = "state", required = false) String stateCode) {
        return ResponseEntity.ok(quoteQueryService.listByRequestIdAndState(requestId, stateCode));
    }
}

