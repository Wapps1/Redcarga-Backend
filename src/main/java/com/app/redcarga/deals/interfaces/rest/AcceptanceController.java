package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.AcceptanceCommandService;
import com.app.redcarga.deals.interfaces.rest.requests.ProposeAcceptanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/deals", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class AcceptanceController {

    private final AcceptanceCommandService acceptanceCommandService;

    @Operation(summary = "Propose an acceptance for a quote")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Acceptance proposed"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping(path = "/quotes/{quoteId}/acceptances", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> proposeAcceptance(
            @PathVariable Integer quoteId,
            @Valid @RequestBody ProposeAcceptanceRequest body,
            JwtAuthenticationToken principal
    ) {
        Integer actorAccountId = Integer.valueOf(principal.getToken().getSubject());
        Integer acceptanceId = acceptanceCommandService.proposeAcceptance(
                quoteId,
                actorAccountId,
                body.idempotencyKey(),
                body.note()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("acceptanceId", acceptanceId));
    }

    @Operation(summary = "Confirm an acceptance")
    @PostMapping(path = "/quotes/{quoteId}/acceptances/{acceptanceId}/confirm")
    public ResponseEntity<?> confirmAcceptance(@PathVariable Integer quoteId,
                                               @PathVariable Integer acceptanceId,
                                               JwtAuthenticationToken principal) {
        Integer actorAccountId = Integer.valueOf(principal.getToken().getSubject());
        acceptanceCommandService.confirmAcceptance(quoteId, acceptanceId, actorAccountId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @Operation(summary = "Reject an acceptance")
    @PostMapping(path = "/quotes/{quoteId}/acceptances/{acceptanceId}/reject")
    public ResponseEntity<?> rejectAcceptance(@PathVariable Integer quoteId,
                                              @PathVariable Integer acceptanceId,
                                              JwtAuthenticationToken principal) {
        Integer actorAccountId = Integer.valueOf(principal.getToken().getSubject());
        acceptanceCommandService.rejectAcceptance(quoteId, acceptanceId, actorAccountId);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
