package com.app.redcarga.identity.infrastructure.events;

import com.app.redcarga.identity.application.internal.outboundservices.events.IdentityEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringIdentityEventPublisher implements IdentityEventPublisher {
    private final ApplicationEventPublisher spring;

    @Override
    public void publish(Object event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    log.info("[IdentityEventPublisher] publish AFTER_COMMIT {}", event.getClass().getSimpleName());
                    spring.publishEvent(event);
                }
            });
        } else {
            log.info("[IdentityEventPublisher] publish immediate {}", event.getClass().getSimpleName());
            spring.publishEvent(event);
        }
    }
}
