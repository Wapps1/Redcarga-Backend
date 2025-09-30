package com.app.redcarga.iam.interfaces.security;

import com.app.redcarga.iam.domain.repositories.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentAccountResolver {

    private final AccountRepository accountRepository;

    public CurrentAccountResolver(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Obtiene el accountId del usuario autenticado.
     * Intenta por externalUid = sub (o user_id), y luego por email como fallback.
     */
    public Integer requireAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Missing JWT");
        }
        String uid = jwt.getClaimAsString("sub");
        if (uid == null || uid.isBlank()) {
            uid = jwt.getClaimAsString("user_id"); // Firebase a veces usa user_id alias
        }

        if (uid != null && !uid.isBlank()) {
            return accountRepository.findByExternalUid(uid)
                    .map(a -> a.getId())
                    .orElseGet(() -> tryResolveByEmail(jwt));
        }
        return tryResolveByEmail(jwt);
    }

    private Integer tryResolveByEmail(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Cannot resolve account from token");
        }
        return accountRepository.findByEmail(email)
                .map(a -> a.getId())
                .orElseThrow(() -> new IllegalStateException("Account not found for email"));
    }
}
