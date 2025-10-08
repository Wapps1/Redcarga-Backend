package com.app.redcarga.requests.application.internal.jobs;

import java.time.Duration;

/**
 * Contrato del job/worker que procesa el outbox de matching.
 * Orquesta:
 *  - fetchAndLeaseNextBatch(...)
 *  - invocar PlanningMatchingClient.matchAndNotify(...)
 *  - markSent(...) / markFailed(...)
 *
 * Implementación típica: un @Service con @Transactional
 * programado por @Scheduled (o lanzado manualmente en dev).
 */
public interface OutboxPlanningMatchJob {

    /**
     * Procesa un batch de tareas “debidas” aplicando lease y backoff.
     *
     * @param maxBatchSize  tope de tareas a procesar en esta corrida
     * @param lease         duración del lease para evitar doble toma
     * @param backoff       incremento de espera al fallar (p. ej., exponencial afuera)
     * @param maxAttempts   máximo de reintentos antes de marcar DEAD
     */
    void runDueBatch(int maxBatchSize, Duration lease, Duration backoff, int maxAttempts);
}
