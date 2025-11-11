package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.AssignmentCommandService;
import com.app.redcarga.deals.domain.services.AssignmentQueryService;
import com.app.redcarga.deals.domain.services.QuoteCommandService;
import com.app.redcarga.deals.domain.services.QuoteQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROVIDER')")
@SecurityRequirement(name = "iam")
public class AssignmentsController {

    private final AssignmentCommandService assignmentCommandService;
    private final AssignmentQueryService assignmentQueryService;

    public record AssignRequest(
            @NotNull Integer driverId,
            @NotNull Integer vehicleId
    ) {}

    public record AssignEditRequest(
        @NotNull Integer driverId,
        @NotNull Integer vehicleId,
        @NotNull Integer version
    ) {}

    @Operation(summary = "Assign a fleet to a quote")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assignment created"),
        @ApiResponse(responseCode = "404", description = "quote or checklist instance not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "quote not accepted", content = @Content),
    })
    @PostMapping("/{quoteId}/assignment")
    public ResponseEntity<?> assign(@PathVariable Integer quoteId,
                    @Valid @RequestBody AssignRequest body,
                    JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        assignmentCommandService.assignToQuote(quoteId, body.driverId(), body.vehicleId(), accountId);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @Operation(summary = "Edit assignment (optimistic version)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assignment updated"),
        @ApiResponse(responseCode = "404", description = "assignment or quote not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "version mismatch or conflict", content = @Content),
        @ApiResponse(responseCode = "422", description = "quote not accepted", content = @Content),
    })
    @PutMapping("/{quoteId}/assignment")
    public ResponseEntity<?> edit(@PathVariable Integer quoteId,
                  @Valid @RequestBody AssignEditRequest body,
                  JwtAuthenticationToken principal) {
    Integer accountId = Integer.valueOf(principal.getToken().getSubject());
    assignmentCommandService.editAssignment(quoteId, body.driverId(), body.vehicleId(), body.version(), accountId);
    return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @Operation(summary = "Unassign fleet from quote")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unassigned"),
        @ApiResponse(responseCode = "404", description = "quote or checklist instance not found", content = @Content),
        @ApiResponse(responseCode = "422", description = "quote not accepted", content = @Content),
    })
    @DeleteMapping("/{quoteId}/assignment")
    public ResponseEntity<?> unassign(@PathVariable Integer quoteId,
                      JwtAuthenticationToken principal) {
    Integer accountId = Integer.valueOf(principal.getToken().getSubject());
    assignmentCommandService.unassignFromQuote(quoteId, accountId);
    return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @Operation(summary = "Get current assignment version for a quote")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Version returned", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"version\":0}"))),
        @ApiResponse(responseCode = "404", description = "assignment not found", content = @Content)
    })
    @GetMapping("/{quoteId}/assignment/version")
    public ResponseEntity<?> getVersion(@PathVariable Integer quoteId) {
    return assignmentQueryService.getVersionByQuoteId(quoteId)
        .map(v -> ResponseEntity.ok(java.util.Map.of("version", v)))
        .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
