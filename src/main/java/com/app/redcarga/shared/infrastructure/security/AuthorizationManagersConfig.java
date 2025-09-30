package com.app.redcarga.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
public class AuthorizationManagersConfig {

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> iamOnly(IssuerCheck ic) {
        return (authentication, context) -> {
            var a = authentication.get(); // a es Authentication
            boolean ok = (a != null) && ic.isIam(a);
            return new AuthorizationDecision(ok);
        };
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> firebaseOrIam(IssuerCheck ic) {
        return (authentication, context) -> {
            var a = authentication.get(); // a es Authentication
            boolean ok = (a != null) && (ic.isFirebase(a) || ic.isIam(a));
            return new AuthorizationDecision(ok);
        };
    }
}
