package com.app.redcarga.shared.infrastructure.ws;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Escucha eventos de suscripción denegada y envía un mensaje inmediato al usuario
 * para que el cliente reciba feedback y no quede esperando silenciosamente.
 */
@Component
public class WsAccessDeniedNotifier {
    private final SimpMessagingTemplate template;

    public WsAccessDeniedNotifier(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void onDenied(WsSubscribeDeniedEvent ev) {
        try {
            template.convertAndSendToUser(
                    ev.userId(),
                    "/queue/system/errors",
                    Map.of(
                            "type", "ACCESS_DENIED",
                            "reason", ev.reason(),
                            "dest", ev.destination(),
                            "ts", ev.timestamp()
                    )
            );
        } catch (Exception ex) {
            System.err.println("[WS] fallo al enviar error a usuario=" + ev.userId() + " ex=" + ex);
        }
    }
}
