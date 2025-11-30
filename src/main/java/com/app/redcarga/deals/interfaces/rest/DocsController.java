package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.model.entities.Guide;
import com.app.redcarga.deals.domain.model.valueobjects.GuideType;
import com.app.redcarga.deals.domain.services.DocsCommandService;
import com.app.redcarga.deals.domain.services.DocsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals/{quoteId}/docs")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class DocsController {

    private final DocsCommandService docsCommandService;
    private final DocsQueryService docsQueryService;

    @Operation(summary = "Mark DOC_GRE_REMITENTE on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked DOC_GRE_REMITENTE"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content)
    })
    @PostMapping("/gre/remitente")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> markRemitente(@PathVariable Integer quoteId, JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        docsCommandService.markDocGreRemitente(quoteId, accountId);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @Operation(summary = "Mark DOC_GRE_TRANSPORTISTA on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked DOC_GRE_TRANSPORTISTA"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content)
    })
    @PostMapping("/gre/transportista")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> markTransportista(@PathVariable Integer quoteId, JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        docsCommandService.markDocGreTransportista(quoteId, accountId);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @Operation(summary = "Create a new guide for a quote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guide created"),
            @ApiResponse(responseCode = "409", description = "Guide already exists", content = @Content),
            @ApiResponse(responseCode = "400", description = "Quote not accepted", content = @Content),
            @ApiResponse(responseCode = "404", description = "quote not found", content = @Content)
    })
    @PostMapping("/guides")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<Guide> createGuide(
            @PathVariable Integer quoteId,
            @RequestParam GuideType type,
            @RequestParam String guideUrl) {
        Guide guide = docsCommandService.createGuide(type, quoteId, guideUrl);
        return ResponseEntity.ok(guide);
    }

    @Operation(summary = "Update guide URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guide URL updated"),
            @ApiResponse(responseCode = "404", description = "guide not found", content = @Content)
    })
    @PatchMapping("/guides/{guideId}/url")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<Guide> updateGuideUrl(
            @PathVariable Integer quoteId,
            @PathVariable Integer guideId,
            @RequestParam String guideUrl) {
        Guide guide = docsCommandService.updateGuideUrl(guideId, guideUrl);
        return ResponseEntity.ok(guide);
    }

    @Operation(summary = "Get all guides for a quote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guides retrieved"),
            @ApiResponse(responseCode = "404", description = "quote not found", content = @Content)
    })
    @GetMapping("/guides")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<List<Guide>> getGuides(@PathVariable Integer quoteId) {
        List<Guide> guides = docsQueryService.getGuidesByQuoteId(quoteId);
        return ResponseEntity.ok(guides);
    }

    @Operation(summary = "Get TRANSPORTISTA guide for a quote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guide retrieved"),
            @ApiResponse(responseCode = "404", description = "guide not found", content = @Content)
    })
    @GetMapping("/guides/transportista")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<Guide> getTransportistaGuide(@PathVariable Integer quoteId) {
        return docsQueryService.getGuideByQuoteIdAndType(quoteId, GuideType.TRANSPORTISTA)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get REMISION guide for a quote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guide retrieved"),
            @ApiResponse(responseCode = "404", description = "guide not found", content = @Content)
    })
    @GetMapping("/guides/remision")
    @PreAuthorize("hasAnyRole('CLIENT', 'PROVIDER')")
    public ResponseEntity<Guide> getRemisionGuide(@PathVariable Integer quoteId) {
        return docsQueryService.getGuideByQuoteIdAndType(quoteId, GuideType.REMISION)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
