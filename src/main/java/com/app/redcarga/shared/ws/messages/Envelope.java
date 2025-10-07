package com.app.redcarga.shared.ws.messages;

import java.time.OffsetDateTime;

/** Sobre estándar para todos los mensajes WS. */
public record Envelope<T>(String type, T data, String at) {
    public static <T> Envelope<T> of(String type, T data) {
        return new Envelope<>(type, data, OffsetDateTime.now().toString());
    }
}
