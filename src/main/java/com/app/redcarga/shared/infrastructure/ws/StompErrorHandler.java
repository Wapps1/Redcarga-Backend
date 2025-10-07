package com.app.redcarga.shared.infrastructure.ws;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.messaging.support.MessageBuilder;

public class StompErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        org.slf4j.LoggerFactory.getLogger(StompErrorHandler.class)
            .error("[WS ERROR] Procesando mensaje: {}", ex.getMessage(), ex);
        return buildError(ex);
    }

    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, 
                                             byte[] errorPayload, 
                                             Throwable cause, 
                                             StompHeaderAccessor clientHeaderAccessor) {
        // Si es AccessDeniedException, NO cerrar la conexión
        if (cause instanceof AccessDeniedException) {
            org.slf4j.LoggerFactory.getLogger(StompErrorHandler.class)
                .warn("[WS ERROR] Acceso denegado, enviando ERROR sin cerrar conexión: {}", cause.getMessage());
            return buildError(cause);
        }
        // Para otros errores, usar el comportamiento por defecto
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }

    private Message<byte[]> buildError(Throwable ex) {
        String text = (ex instanceof AccessDeniedException)
                ? "FORBIDDEN: " + ex.getMessage()
                : "BAD_REQUEST: " + (ex.getMessage() == null ? "processing error" : ex.getMessage());

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setContentType(MimeTypeUtils.TEXT_PLAIN);
        accessor.setLeaveMutable(true);
        return MessageBuilder.withPayload(text.getBytes()).setHeaders(accessor).build();
    }
}
