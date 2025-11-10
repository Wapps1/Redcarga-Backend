package com.app.redcarga.requests.application.internal.jobs;

/** Contract for processing request.accepted events from deals outbox. */
public interface OutboxRequestAcceptedJob {
    void runBatch(int maxBatchSize);
}
