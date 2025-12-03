package com.app.redcarga.tracking.interfaces.rest;

import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.tracking.application.internal.outboundservices.acl.DealsFacadeClient;
import com.app.redcarga.tracking.domain.services.TrackingQueryService;
import com.app.redcarga.tracking.interfaces.rest.responses.CurrentLocationResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking/quotes")
@SecurityRequirement(name = "iam")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingQueryService queryService;
    private final DealsFacadeClient deals;

    @GetMapping("/{quoteId}/current-location")
    public ResponseEntity<CurrentLocationResponse> getCurrentLocation(@PathVariable int quoteId, JwtAuthenticationToken principal) {

        int accountId = Integer.valueOf(principal.getToken().getSubject());

        boolean isParticipant = deals.isChatParticipant(quoteId, accountId);
        boolean isDriver = deals.isDriverOfQuote(quoteId, accountId);

        if (!isDriver && !isParticipant) {
            throw new DomainException("not_authorized_for_quote");
        }

        return queryService.getCurrentLocationByQuoteId(quoteId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new DomainException("location_not_found"));
    }
}
