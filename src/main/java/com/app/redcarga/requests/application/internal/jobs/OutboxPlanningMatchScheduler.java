// requests/application/internal/jobs/OutboxPlanningMatchScheduler.java
package com.app.redcarga.requests.application.internal.jobs;

import com.app.redcarga.requests.application.internal.config.RequestsOutboxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPlanningMatchScheduler {

    private final OutboxPlanningMatchJob job;
    private final RequestsOutboxProperties props;

    // lee directamente de application.properties
    @Scheduled(cron = "${requests.outbox.match.cron}")
    public void tick() {
        if (!props.isEnabled()) return;
        job.runDueBatch(props.getBatchSize(), props.getLease(), props.getBackoff(), props.getMaxAttempts());
    }
}
