package com.app.redcarga.iam.application.internal.eventhandlers;

import com.app.redcarga.iam.application.internal.eventhandlers.KycEventsHandler;
import com.app.redcarga.iam.application.internal.integration.contracts.ClientKycPassed;
import com.app.redcarga.iam.application.internal.integration.contracts.ProviderKycPassed;
import com.app.redcarga.iam.application.internal.integration.contracts.ProviderOnboarded;

import com.app.redcarga.shared.events.identity.ClientKycPassedEvent;
import com.app.redcarga.shared.events.identity.ProviderKycPassedEvent;
// import com.app.redcarga.shared.events.providers.ProviderOnboardedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityEventsAdapter {

    private final KycEventsHandler handler;

    @EventListener
    public void onClient(ClientKycPassedEvent e) {
        log.info("[IAM] onClient AFTER_COMMIT {}", e);
        handler.onClientKycPassed(new ClientKycPassed(
                toUuid(e.eventId()),                // String -> UUID
                toInstant(e.occurredAt()),          // String/Instant -> Instant (ver método abajo)
                Integer.valueOf(e.accountId()),     // int -> Integer
                Integer.valueOf(e.schemaVersion())  // int -> Integer
        ));
    }

    @EventListener
    public void onProvider(ProviderKycPassedEvent e) {
        handler.onProviderKycPassed(new ProviderKycPassed(
                toUuid(e.eventId()),
                toInstant(e.occurredAt()),
                Integer.valueOf(e.accountId()),
                Integer.valueOf(e.schemaVersion())
        ));
    }

    /*
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProviderOnboarded(ProviderOnboardedEvent e) {
        handler.onProviderOnboarded(new ProviderOnboarded(
                toUuid(e.eventId()),
                toInstant(e.occurredAt()),
                Integer.valueOf(e.accountId()),
                Integer.valueOf(e.schemaVersion())
        ));
    }
    */

    // --- helpers ---

    private static UUID toUuid(Object v) {
        if (v instanceof UUID u) return u;
        return UUID.fromString(String.valueOf(v));
    }

    private static Instant toInstant(Object v) {
        // Usa la rama correspondiente según tu tipo real:
        if (v instanceof Instant i) return i;          // ← si en shared ya es Instant
        return Instant.parse(String.valueOf(v));       // ← si en shared es String ISO-8601
    }
}
