package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.ShipmentCommandService;
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
@RequestMapping("/api/deals/{quoteId}/shipment")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class ShipmentsController {

    private final ShipmentCommandService shipmentCommandService;

    @Operation(summary = "Mark SHIPMENT_RECEIVED on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked SHIPMENT_RECEIVED"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content)
    })
    @PostMapping("/received")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> markReceived(@PathVariable Integer quoteId,
                                          JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        shipmentCommandService.markShipmentReceived(quoteId, accountId);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }

    @Operation(summary = "Mark SHIPMENT_SENT on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked SHIPMENT_SENT"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content)
    })
    @PostMapping("/sent")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> markSent(@PathVariable Integer quoteId,
                                      JwtAuthenticationToken principal) {
        Integer accountId = Integer.valueOf(principal.getToken().getSubject());
        shipmentCommandService.markShipmentSent(quoteId, accountId);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }
}