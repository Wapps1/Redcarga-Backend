package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.ChangeCommandService;
import com.app.redcarga.deals.interfaces.rest.requests.ChangeRequest;
import com.app.redcarga.deals.interfaces.rest.responses.ChangeResponse;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/deals/quotes/{quoteId}/changes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class ChangeController {

    private final ChangeCommandService changeCommandService;

    @Operation(summary = "Apply changes (LIBRE) to a quote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Change applied"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Conflict / version mismatch")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChangeResponse applyChanges(
            @PathVariable Integer quoteId,
            @RequestHeader(value = "If-Match", required = false) Integer ifMatch,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody ChangeRequest request,
            JwtAuthenticationToken principal
    ) {
        Integer actorAccountId = Integer.valueOf(principal.getToken().getSubject());
    Integer changeId = changeCommandService.decideAndApplyChange(quoteId, request.toDomainItems(), actorAccountId, ifMatch, idempotencyKey);
        return new ChangeResponse(changeId);
    }
}
