package com.app.redcarga.identity.interfaces.rest;

import com.app.redcarga.identity.application.internal.outboundservices.events.IdentityEventPublisher;
import com.app.redcarga.shared.events.identity.ClientKycPassedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

// solo dev
@RestController
@RequestMapping("/_dev/events")
@RequiredArgsConstructor
class DevEventsController {
    private final IdentityEventPublisher publisher;
    @PostMapping("/client/{accountId}")
    void emitClient(@PathVariable int accountId) {
        publisher.publish(new ClientKycPassedEvent("3d9b3e2b-5e6a-4f2a-9fd3-4fb49b6c2d77", Instant.now(), accountId, 1));
    }
}
