package com.app.redcarga.shared.infrastructure.ws;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/_dev/ws")
public class WsDevPublisher {

    private final SimpMessagingTemplate template;

    public WsDevPublisher(SimpMessagingTemplate template) {
        this.template = template;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping(@RequestParam int companyId,
                                    @RequestParam(defaultValue = "ping") String msg) {
        String dest = String.format("/topic/planning/company.%d.solicitudes", companyId);
        Map<String, Object> payload = Map.of(
                "type", "TEST",
                "message", msg,
                "timestamp", System.currentTimeMillis()
        );
        template.convertAndSend(dest, payload);
        return Map.of("sentTo", dest, "payload", payload);
    }
}