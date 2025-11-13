package com.app.redcarga.deals.interfaces.rest;

import com.app.redcarga.deals.domain.services.PaymentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/deals/{quoteId}/payment")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class PaymentsController {

        private final PaymentCommandService paymentCommandService;

    @Operation(summary = "Mark PAYMENT_MADE on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked PAYMENT_MADE"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "missing dependencies", content = @Content(mediaType = "application/json", schema = @Schema(example = "[\"PAYMENT_MADE\"]")))
    })
        @PostMapping("/made")
        @PreAuthorize("hasRole('CLIENT')")
        public ResponseEntity<?> markPaymentMade(@PathVariable Integer quoteId,
                                                                                         JwtAuthenticationToken principal) {
                Integer accountId = Integer.valueOf(principal.getToken().getSubject());
                paymentCommandService.markPaymentMade(quoteId, accountId);
                return ResponseEntity.ok(java.util.Map.of("ok", true));
        }

    @Operation(summary = "Mark PAYMENT_CONFIRMED on the quote's checklist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked PAYMENT_CONFIRMED"),
            @ApiResponse(responseCode = "404", description = "quote or checklist instance/item not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "missing dependencies", content = @Content(mediaType = "application/json", schema = @Schema(example = "[\"PAYMENT_MADE\"]")))
    })
        @PostMapping("/confirm")
        @PreAuthorize("hasRole('PROVIDER')")
        public ResponseEntity<?> markPaymentConfirmed(@PathVariable Integer quoteId,
                                                                                                  JwtAuthenticationToken principal) {
                Integer accountId = Integer.valueOf(principal.getToken().getSubject());
                paymentCommandService.markPaymentConfirmed(quoteId, accountId);
                return ResponseEntity.ok(java.util.Map.of("ok", true));
        }
}
