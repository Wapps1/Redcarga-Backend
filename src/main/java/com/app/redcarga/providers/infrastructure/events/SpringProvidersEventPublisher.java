package com.app.redcarga.providers.infrastructure.events;

import com.app.redcarga.providers.application.internal.outboundservices.events.ProvidersEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringProvidersEventPublisher implements ProvidersEventPublisher {
    private final ApplicationEventPublisher spring;

    @Override
    public void publish(Object event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    log.info("[ProvidersEventPublisher] AFTER_COMMIT {}", event.getClass().getSimpleName());
                    spring.publishEvent(event);
                }
            });
        } else {
            log.info("[ProvidersEventPublisher] immediate {}", event.getClass().getSimpleName());
            spring.publishEvent(event);
        }
    }
}

