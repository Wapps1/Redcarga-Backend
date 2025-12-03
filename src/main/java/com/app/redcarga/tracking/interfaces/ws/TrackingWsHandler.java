package com.app.redcarga.tracking.interfaces.ws;

import com.app.redcarga.shared.infrastructure.ws.AccountPrincipal;
import com.app.redcarga.shared.ws.Destinations;
import com.app.redcarga.tracking.application.internal.commandservices.TrackingCommandServiceImpl;
import com.app.redcarga.tracking.interfaces.ws.messages.LocationBroadcast;
import com.app.redcarga.tracking.interfaces.ws.requests.LocationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TrackingWsHandler {

    private final TrackingCommandServiceImpl commandService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Receives location updates from driver via STOMP SEND.
     * Destination pattern: /app/quotes.{quoteId}.tracking.update
     * 
     * Only the assigned driver can send; validation done in interceptor.
     */
    @MessageMapping("/quotes.{quoteId}.tracking.update")
    public void handleLocationUpdate(
        @DestinationVariable int quoteId,
        @Payload LocationUpdateRequest request,
        Principal principal
    ) {
        AccountPrincipal ap = (AccountPrincipal) principal;
        int accountId = ap.accountId();
        
        // Persist conditionally (threshold handled internally)
        commandService.updateLocationFromAccount(quoteId, accountId, request.lat(), request.lng(), request.speed());
        
        // Broadcast to topic (always, even if not persisted due to threshold)
        String topic = Destinations.topicQuoteTracking(quoteId);
        var broadcast = new LocationBroadcast(
            quoteId,
            accountId, // simplified: use accountId as identifier; can resolve driverId if needed
            request.lat(),
            request.lng(),
            request.speed(),
            Instant.now().toString()
        );
        messagingTemplate.convertAndSend(topic, broadcast);
        
        log.info("Broadcasted location for quote {} from account {}", quoteId, accountId);
    }
}
