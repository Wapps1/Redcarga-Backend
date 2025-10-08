package com.app.redcarga.requests.application.internal.gateways;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Puerto de persistencia para el outbox que garantiza el envío del comando
 * Planning.matchAndNotify(...) aunque falle temporalmente.
 *
 * Implementación: infrastructure.persistence.jdbc.PgOutboxPlanningMatchGateway
 */
public interface OutboxPlanningMatchGateway {

    /**
     * Encola (idempotente por request_id UNIQUE) la tarea de match.
     */
    void enqueueMatch(Integer requestId,
                      String originDepartmentCode, String originProvinceCode,
                      String destDepartmentCode,   String destProvinceCode,
                      Instant createdAt,
                      Instant firstAttemptAt);

    /**
     * Obtiene hasta {@code limit} tareas listas para ejecutar y toma un lease
     * por {@code lease}. El caller debe ejecutar cada tarea y luego marcar
     * SENT o FAILED según corresponda.
     */
    List<MatchTask> fetchAndLeaseNextBatch(int limit, Duration lease, Instant now);

    /** Marca una tarea como enviada (SENT) y libera cualquier lease. */
    void markSent(Integer outboxId);

    /**
     * Marca fallo, incrementa reintentos y agenda el próximo intento con backoff.
     * Si supera {@code maxAttempts}, transiciona a DEAD.
     */
    void markFailed(Integer outboxId, String lastError, Duration backoff, Instant now, int maxAttempts);

    /** Borra por request (útil cuando ya no corresponde reintentar). */
    void deleteByRequestId(Integer requestId);

    /* ================== DTO del puerto ================== */

    /**
     * Tarea tomada del outbox. Contiene todo lo necesario para invocar
     * Planning.matchAndNotify(...) sin ir a otras tablas.
     */
    record MatchTask(
            Integer outboxId,
            Integer requestId,
            String originDepartmentCode,
            String originProvinceCode,
            String destDepartmentCode,
            String destProvinceCode,
            Instant createdAt,
            Instant nextAttemptAt,
            Integer attemptCount,
            String lastError
    ) {}
}
