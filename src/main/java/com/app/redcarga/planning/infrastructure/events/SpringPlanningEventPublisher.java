package com.app.redcarga.planning.infrastructure.events;

import com.app.redcarga.planning.application.internal.outboundservices.events.PlanningEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Publica eventos AFTER_COMMIT cuando hay transacción activa;
 * si no hay TX, publica inmediato.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringPlanningEventPublisher implements PlanningEventPublisher {

    private final ApplicationEventPublisher spring;

    @Override
    public void publish(Object event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    log.debug("[PlanningEventPublisher] AFTER_COMMIT {}", event.getClass().getSimpleName());
                    spring.publishEvent(event);
                }
            });
        } else {
            log.debug("[PlanningEventPublisher] immediate {}", event.getClass().getSimpleName());
            spring.publishEvent(event);
        }
    }
}
