package com.app.redcarga.shared.infrastructure.ws;

import com.app.redcarga.shared.ws.auth.MembershipVerifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Map;

public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(StompAuthChannelInterceptor.class);

    private final MembershipVerifierPort membership;
    private final ApplicationEventPublisher events;

    public StompAuthChannelInterceptor(MembershipVerifierPort membership,
                                       ApplicationEventPublisher events) {
        this.membership = membership;
        this.events = events;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var acc = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(acc.getCommand())) {
            final String dest = acc.getDestination();
            Integer companyId = DestinationPatterns.tryExtractCompanyIdFromCompanySolicitudes(dest);

            if (companyId != null) {
                var user = acc.getUser();
                if (user == null || user.getName() == null) {
                    log.warn("[WS] SUBSCRIBE bloqueado: unauthenticated session (dest={})", dest);
                    notifyAccessDenied(acc, "No autenticado", dest);
                    return null; // descarta sin cerrar socket
                }

                final int accountId;
                try {
                    accountId = Integer.parseInt(user.getName());
                } catch (NumberFormatException e) {
                    log.warn("[WS] SUBSCRIBE bloqueado: invalid principal (dest={})", dest);
                    notifyAccessDenied(acc, "Principal inválido", dest);
                    return null;
                }

                final boolean allowed;
                try {
                    allowed = membership.isActiveMember(companyId, accountId);
                } catch (Exception e) {
                    log.warn("[WS] SUBSCRIBE bloqueado: membership check failed (dest={}, ex={})", dest, e.toString());
                    notifyAccessDenied(acc, "Error al verificar membresía", dest);
                    return null;
                }

                if (!allowed) {
                    log.warn("[WS] SUBSCRIBE bloqueado: not member (companyId={}, accountId={})", companyId, accountId);
                    notifyAccessDenied(acc, "No eres miembro de la compañía #" + companyId, dest);
                    return null; // bloquear suscripción, el cliente verá timeout salvo que escuche /user/queue/system/errors
                }
            }
        }
        return message; // dejar pasar los demás
    }

    private void notifyAccessDenied(StompHeaderAccessor accessor, String reason, String destination) {
        if (accessor.getUser() == null || accessor.getUser().getName() == null) return;

        events.publishEvent(new WsSubscribeDeniedEvent(
                accessor.getUser().getName(),
                reason,
                destination == null ? "unknown" : destination,
                System.currentTimeMillis()
        ));
    }
}
