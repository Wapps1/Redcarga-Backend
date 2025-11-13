package com.app.redcarga.shared.ws.auth;

/**
 * Puerto para que el BC Deals verifique si un accountId puede suscribirse
 * al tópico de chat de una quote.
 */
public interface ChatSubscriptionVerifierPort {
    boolean canSubscribeToQuote(int quoteId, int accountId);
}
