package com.app.redcarga.requests.application.internal.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxRequestAcceptedScheduler {

    private final OutboxRequestAcceptedJob job;

    // Poll cada 2s; ajustable con property
    @Scheduled(fixedDelayString = "${outbox.requests.accepted.poll.ms:2000}")
    public void poll() {
        job.runBatch(50);
    }
}
