package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.DocsCommandService;
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

@RestController
@RequestMapping("/api/deals/{quoteId}/docs/gre")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class DocsController {

    private final DocsCommandService docsCommandService;

    @Operation(summary = "Mark DOC_GRE_REMITENTE on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked DOC_GRE_REMITENTE"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content)
    })
    @PostMapping("/remitente")
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
    @PostMapping("/transportista")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> markTransportista(@PathVariable Integer quoteId, JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        docsCommandService.markDocGreTransportista(quoteId, accountId);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }
}
