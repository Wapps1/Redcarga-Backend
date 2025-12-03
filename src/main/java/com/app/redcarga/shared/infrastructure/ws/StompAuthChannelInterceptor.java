package com.app.redcarga.shared.infrastructure.ws;

import com.app.redcarga.shared.ws.auth.DriverQuoteAssignmentVerifierPort;
import com.app.redcarga.shared.ws.auth.MembershipVerifierPort;
import com.app.redcarga.shared.ws.auth.RequestOwnershipVerifierPort;
import com.app.redcarga.shared.ws.auth.ChatSubscriptionVerifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * Interceptor que valida subscripciones protegidas contra el MembershipVerifierPort.
 * Cuando deniega, publica un evento interno (WsSubscribeDeniedEvent). Un listener separado
 * (WsAccessDeniedNotifier) es quien envía el mensaje al usuario para evitar ciclos de beans.
 */
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(StompAuthChannelInterceptor.class);

    private final MembershipVerifierPort membership;
    private final RequestOwnershipVerifierPort requestOwnershipVerifier;
    private final ApplicationEventPublisher events;
    private final ChatSubscriptionVerifierPort chatSubscriptionVerifier;
    private final DriverQuoteAssignmentVerifierPort driverQuoteAssignmentVerifier;


    public StompAuthChannelInterceptor(MembershipVerifierPort membership,
                                       RequestOwnershipVerifierPort requestOwnershipVerifier,
                                       ApplicationEventPublisher events,
                                       ChatSubscriptionVerifierPort chatSubscriptionVerifier,
                                       DriverQuoteAssignmentVerifierPort driverQuoteAssignmentVerifier) {
        this.membership = membership;
        this.requestOwnershipVerifier = requestOwnershipVerifier;
        this.events = events;
        this.chatSubscriptionVerifier = chatSubscriptionVerifier;
        this.driverQuoteAssignmentVerifier = driverQuoteAssignmentVerifier;
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
                    notifyAccessDenied(null, "No autenticado", dest);
                    return null; // descarta sin cerrar socket
                }

                final int accountId;
                try {
                    accountId = Integer.parseInt(user.getName());
                } catch (NumberFormatException e) {
                    log.warn("[WS] SUBSCRIBE bloqueado: invalid principal (dest={})", dest);
                    notifyAccessDenied(user.getName(), "Principal inválido", dest);
                    return null;
                }

                final boolean allowed;
                long t0 = System.currentTimeMillis();
                try {
                    allowed = membership.isActiveMember(companyId, accountId);
                } catch (Exception e) {
                    log.warn("[WS] SUBSCRIBE bloqueado: membership check failed (dest={}, ex={})", dest, e.toString());
                    notifyAccessDenied(user.getName(), "Error al verificar membresía", dest);
                    return null;
                }
                long ms = System.currentTimeMillis() - t0;
                log.info("[WS-Auth] cmp={} acc={} allowed={} took={}ms dest={}", companyId, accountId, allowed, ms, dest);

                if (!allowed) {
                    log.warn("[WS] SUBSCRIBE bloqueado: not member (companyId={}, accountId={})", companyId, accountId);
                    notifyAccessDenied(user.getName(), "No eres miembro de la compañía #" + companyId, dest);
                    return null; // bloquear suscripción, el cliente verá el mensaje en /user/queue/system/errors
                }
            }

            // Validate subscriber for requests account quotes topic
            Integer accountIdFromRequestTopic = DestinationPatterns.tryExtractAccountIdFromRequestsQuotes(dest);
            if (accountIdFromRequestTopic != null) {
                var user = acc.getUser();
                if (user == null || user.getName() == null) {
                    notifyAccessDenied(null, "No autenticado", dest);
                    return null;
                }
                final int accountId;
                try {
                    accountId = Integer.parseInt(user.getName());
                } catch (NumberFormatException e) {
                    notifyAccessDenied(user.getName(), "Principal inválido", dest);
                    return null;
                }
                if (accountId != accountIdFromRequestTopic) {
                    notifyAccessDenied(user.getName(), "Cuenta no coincide con destino", dest);
                    return null;
                }
                // Optional requestId header to verify ownership of a specific request
                String reqHdr = acc.getFirstNativeHeader("requestId");
                if (reqHdr != null && !reqHdr.isBlank() && requestOwnershipVerifier != null) {
                    try {
                        Integer requestId = Integer.parseInt(reqHdr);
                        boolean isRequester = requestOwnershipVerifier.isRequester(requestId, accountId);
                        if (!isRequester) {
                            notifyAccessDenied(user.getName(), "No eres el requester de la requestId=" + requestId, dest);
                            return null;
                        }
                    } catch (Exception ex) {
                        notifyAccessDenied(user.getName(), "Error al verificar request ownership", dest);
                        return null;
                    }
                }
            }

            // Validate subscriber for deals quotes chat topic
            Integer quoteIdFromChat = DestinationPatterns.tryExtractQuoteIdFromDealsQuotesChat(dest);
            if (quoteIdFromChat != null) {
                var user = acc.getUser();
                if (user == null || user.getName() == null) {
                    notifyAccessDenied(null, "No autenticado", dest);
                    return null;
                }
                final int accountId;
                try {
                    accountId = Integer.parseInt(user.getName());
                } catch (NumberFormatException e) {
                    notifyAccessDenied(user.getName(), "Principal inválido", dest);
                    return null;
                }
                boolean allowed;
                try {
                    allowed = chatSubscriptionVerifier.canSubscribeToQuote(quoteIdFromChat, accountId);
                } catch (Exception ex) {
                    notifyAccessDenied(user.getName(), "Error al verificar acceso al chat", dest);
                    return null;
                }
                if (!allowed) {
                    notifyAccessDenied(user.getName(), "No autorizado para chat de quote=" + quoteIdFromChat, dest);
                    return null;
                }
            }

            // Validate subscriber for tracking topic
            var trackingQuoteId = DestinationPatterns.extractQuoteIdFromTrackingTopic(dest);
            if (trackingQuoteId.isPresent()) {
                var user = acc.getUser();
                if (user == null || user.getName() == null) {
                    notifyAccessDenied(null, "No autenticado", dest);
                    return null;
                }
                final int accountId;
                try {
                    accountId = Integer.parseInt(user.getName());
                } catch (NumberFormatException e) {
                    notifyAccessDenied(user.getName(), "Principal inválido", dest);
                    return null;
                }
                int qid = trackingQuoteId.get();
                boolean canAccess;
                try {
                    canAccess = chatSubscriptionVerifier.canSubscribeToQuote(qid, accountId)
                             || driverQuoteAssignmentVerifier.isDriverOfQuote(qid, accountId);
                } catch (Exception ex) {
                    notifyAccessDenied(user.getName(), "Error al verificar acceso al tracking", dest);
                    return null;
                }
                if (!canAccess) {
                    notifyAccessDenied(user.getName(), "No autorizado para tracking de quote=" + qid, dest);
                    return null;
                }
            }
        }

        // Validate SEND frames to tracking update endpoint
        if (StompCommand.SEND.equals(acc.getCommand())) {
            final String dest = acc.getDestination();
            var trackingQuoteId = DestinationPatterns.extractQuoteIdFromAppTrackingUpdate(dest);
            if (trackingQuoteId.isPresent()) {
                var user = acc.getUser();
                if (user == null || user.getName() == null) {
                    notifyAccessDenied(null, "No autenticado", dest);
                    return null;
                }
                final int accountId;
                try {
                    accountId = Integer.parseInt(user.getName());
                } catch (NumberFormatException e) {
                    notifyAccessDenied(user.getName(), "Principal inválido", dest);
                    return null;
                }
                int qid = trackingQuoteId.get();
                boolean isDriver;
                try {
                    isDriver = driverQuoteAssignmentVerifier.isDriverOfQuote(qid, accountId);
                } catch (Exception ex) {
                    notifyAccessDenied(user.getName(), "Error al verificar driver", dest);
                    return null;
                }
                if (!isDriver) {
                    log.warn("[WS] SEND bloqueado: account {} not driver for quote {}", accountId, qid);
                    notifyAccessDenied(user.getName(), "No eres el driver asignado a quote=" + qid, dest);
                    return null;
                }
            }
        }
        return message; // dejar pasar los demás
    }

    private void notifyAccessDenied(String username, String reason, String destination) {
        if (username == null) return;
        events.publishEvent(new WsSubscribeDeniedEvent(
                username,
                reason,
                destination == null ? "unknown" : destination,
                System.currentTimeMillis()
        ));
    }
}
