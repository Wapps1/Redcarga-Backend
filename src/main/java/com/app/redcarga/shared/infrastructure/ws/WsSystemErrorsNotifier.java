package com.app.redcarga.shared.infrastructure.ws;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WsSystemErrorsNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public WsSystemErrorsNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void onDenied(WsSubscribeDeniedEvent e) {
        var payload = Map.of(
                "type", "SUBSCRIBE_DENIED",
                "reason", e.reason(),
                "destination", e.destination(),
                "timestamp", e.timestamp()
        );
        messagingTemplate.convertAndSendToUser(e.userId(), "/queue/system/errors", payload);
    }
}
