package com.app.redcarga.shared.infrastructure.ws;

import java.security.Principal;

/** Principal mínimo para enrutar /user/queue y autorizar suscripciones. */
public record AccountPrincipal(int accountId) implements Principal {
    @Override public String getName() { return String.valueOf(accountId); }
}
