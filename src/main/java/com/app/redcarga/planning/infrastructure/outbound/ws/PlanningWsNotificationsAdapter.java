package com.app.redcarga.planning.infrastructure.outbound.ws;

import com.app.redcarga.planning.application.internal.outboundservices.notifications.NewRequestNotification;
import com.app.redcarga.planning.application.internal.outboundservices.notifications.NotificationsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adapter de publicación por STOMP.
 * No maneja transacciones: se invoca desde handlers AFTER_COMMIT.
 */
@Component
@RequiredArgsConstructor
public class PlanningWsNotificationsAdapter implements NotificationsPort {

    private final SimpMessagingTemplate template;

    private static String companyTopic(int companyId) {
        // Si tienes shared.infrastructure.ws.Destinations, úsalo aquí.
        return String.format("/topic/planning/company.%d.solicitudes", companyId);
    }

    @Override
    public void notifyNewRequest(NewRequestNotification n) {
        template.convertAndSend(companyTopic(n.companyId()), n);
    }

    @Override
    public void notifyRequestClosed(int requestId, int companyId) {
        // Placeholder para futuro: cerrar/actualizar en UI si fuese necesario.
        template.convertAndSend(companyTopic(companyId),
                new SystemMessage("REQUEST_CLOSED", requestId));
    }

    // DTO simple para notificación de cierre (puedes reemplazarlo por un DTO formal luego).
    private record SystemMessage(String type, int requestId) {}
}
