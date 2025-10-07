package com.app.redcarga.planning.application.internal.gateways;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de aplicación para escritura idempotente en planning.request_inbox.
 * Implementación: infrastructure.persistence.jdbc.PgRequestInboxStore
 */
public interface RequestInboxStore {

    void insertIfAbsent(int requestId,
                        int companyId,
                        int routeId,
                        int routeTypeId,
                        Instant createdAt);

    List<Integer> closeAllForRequest(int requestId);
}
