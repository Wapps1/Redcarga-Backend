package com.app.redcarga.planning.application.internal.outboundservices.notifications;

/**
 * Puerto para side-effects de notificación (WS/STOMP, email, etc.).
 * Implementación WS: infrastructure.outbound.ws.PlanningWsNotificationsAdapter
 */
public interface NotificationsPort {

    void notifyNewRequest(NewRequestNotification n);

    default void notifyRequestClosed(int requestId, int companyId) {
        // reservado para siguientes iteraciones
    }
}
