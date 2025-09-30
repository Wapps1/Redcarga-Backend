package com.app.redcarga.iam.application.internal.gateways;

import java.util.Map;

public interface AuthProviderGateway {
    /** Crea usuario y retorna el UID externo. */
    String createUser(String email, char[] rawPassword);

    /** Asigna custom claims (ej. sys_roles, username). */
    void setCustomClaims(String externalUid, Map<String, Object> claims);

    /** Genera el link de verificación de email con continueUrl; tú decides cómo enviarlo. */
    String generateEmailVerificationLink(String email, String continueUrl);

    /** Revoca refresh tokens para forzar re-login en todos los dispositivos. */
    void revokeRefreshTokens(String externalUid);

    /** Consulta al IdP (Firebase) q el email ya fue verificado. */
    boolean isEmailVerified(String email);
}
