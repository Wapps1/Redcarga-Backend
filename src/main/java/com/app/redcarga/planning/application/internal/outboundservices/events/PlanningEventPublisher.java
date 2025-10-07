package com.app.redcarga.planning.application.internal.outboundservices.events;

/**
 * Puerto para publicar eventos internos AFTER_COMMIT.
 * Implementación: infrastructure.events.SpringPlanningEventPublisher
 */
public interface PlanningEventPublisher {
    void publish(Object event);
}
