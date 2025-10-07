package com.app.redcarga.planning.application.internal.commandservices;

import com.app.redcarga.planning.application.internal.gateways.RequestInboxStore;
import com.app.redcarga.planning.application.internal.outboundservices.notifications.NotificationsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RequestInboxCloseService {

    private final RequestInboxStore inbox;
    private final NotificationsPort notifications;
    private final Clock clock;

    @Transactional
    public void closeInboxForRequest(int requestId, boolean notify) {
        var companies = inbox.closeAllForRequest(requestId);
        if (!notify || companies.isEmpty()) return;

        Instant closedAt = Instant.now(clock);
        for (Integer companyId : companies) {
            notifications.notifyRequestClosed(requestId, companyId);
            // Si quieres enviar el closedAt, puedes ajustar notifyRequestClosed para incluirlo.
        }
    }
}
