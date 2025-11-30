package com.app.redcarga.shared.ws.auth;

public interface DriverQuoteAssignmentVerifierPort {
    /**
     * Verifica si el accountId corresponde al driver asignado a la quote.
     * @param quoteId ID de la cotización
     * @param accountId ID de la cuenta del usuario conectado
     * @return true si el accountId es el driver asignado; false en otro caso
     *
     * boolean isDriverOfQuote(int quoteId, int accountId);
     */

    boolean isDriverOfQuote(int quoteId, int accountId);

}