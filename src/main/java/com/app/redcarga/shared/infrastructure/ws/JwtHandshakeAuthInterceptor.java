package com.app.redcarga.shared.infrastructure.ws;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Valida el JWT en el handshake.
 * - Soporta Authorization: Bearer <token> y ?access_token=<token>
 * - Si es válido, coloca AccountPrincipal(accountId) en atributos para el HandshakeHandler.
 */
public class JwtHandshakeAuthInterceptor implements HandshakeInterceptor {

    private final JwtDecoder jwtDecoder;
    private final String iamIssuer;

    public JwtHandshakeAuthInterceptor(JwtDecoder jwtDecoder, String iamIssuer) {
        this.jwtDecoder = jwtDecoder;
        this.iamIssuer = iamIssuer;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String token = resolveToken(req).orElse(null);
            if (token == null) return false;

            Jwt jwt = jwtDecoder.decode(token); // valida firma, exp, issuer/audience (según tu decoder)
            Integer accountId = extractAccountId(jwt);
            if (accountId == null) return false;

            attributes.put("principal", new AccountPrincipal(accountId));
            return true;
        } catch (Exception ex) {
            return false; // rechaza el upgrade
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest req, ServerHttpResponse res,
                               WebSocketHandler wsHandler, Exception ex) { }

    private Optional<String> resolveToken(ServerHttpRequest req) {
        List<String> auth = req.getHeaders().get("Authorization");
        if (auth != null && !auth.isEmpty() && auth.get(0).startsWith("Bearer "))
            return Optional.of(auth.get(0).substring(7));

        String query = req.getURI().getQuery();
        if (query != null) {
            for (String p : query.split("&")) {
                int eq = p.indexOf('=');
                if (eq > 0) {
                    String k = p.substring(0, eq);
                    String v = p.substring(eq + 1);
                    if ("access_token".equalsIgnoreCase(k) && !v.isBlank()) return Optional.of(v);
                }
            }
        }
        return Optional.empty();
    }

    /** Extrae accountId: primero 'account_id'; si no existe y el 'iss' es IAM, usa 'sub' como entero. */
    private Integer extractAccountId(Jwt jwt) {
        Object v = jwt.getClaim("account_id");
        if (v instanceof Integer i) return i;
        if (v instanceof String s) {
            try { return Integer.parseInt(s); } catch (NumberFormatException ignore) {}
        }
        String iss = (String) jwt.getClaims().get("iss");
        if (iss != null && !iss.isBlank() && iss.equals(iamIssuer)) {
            try { return Integer.parseInt(jwt.getSubject()); } catch (NumberFormatException ignore) {}
        }
        return null;
    }
}
