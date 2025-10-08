package com.app.redcarga.requests.application.internal.jobs;

import com.app.redcarga.requests.application.internal.gateways.OutboxPlanningMatchGateway;
import com.app.redcarga.requests.application.internal.jobs.OutboxPlanningMatchJob;
import com.app.redcarga.requests.application.internal.outboundservices.acl.IdentityPersonService;
import com.app.redcarga.requests.application.internal.outboundservices.acl.PlanningMatchingClient;
import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPlanningMatchJobImpl implements OutboxPlanningMatchJob {

    private final OutboxPlanningMatchGateway outbox;
    private final PlanningMatchingClient planning;
    private final RequestRepository requests;
    private final IdentityPersonService identity;

    @Override
    @Transactional
    public void runDueBatch(int maxBatchSize, Duration lease, Duration backoff, int maxAttempts) {
        Instant now = Instant.now();

        List<OutboxPlanningMatchGateway.MatchTask> tasks =
                outbox.fetchAndLeaseNextBatch(maxBatchSize, lease, now);

        if (tasks.isEmpty()) {
            log.debug("[OutboxMatch] no tasks due");
            return;
        }

        for (var t : tasks) {
            try {
                // 1) Cargar snapshot de nombre desde la Request (fuente principal)
                Optional<Request> maybeReq = requests.findById(t.requestId());
                if (maybeReq.isEmpty()) {
                    log.warn("[OutboxMatch] request {} not found, deleting outbox {}", t.requestId(), t.outboxId());
                    outbox.deleteByRequestId(t.requestId());
                    continue;
                }
                Request req = maybeReq.get();

                String requesterName = safeName(req.getRequesterNameSnapshot())
                        .orElseGet(() -> identity.getByAccountId(req.getRequesterAccountId())
                                .map(IdentityPersonService.PersonSnapshot::fullName)
                                .orElse(""));

                // 2) Invocar Planning (bean-to-bean) con A→B, createdAt y nombre
                planning.matchAndNotify(
                        t.requestId(),
                        t.originDepartmentCode(), t.originProvinceCode(),
                        t.destDepartmentCode(),   t.destProvinceCode(),
                        t.createdAt(),            requesterName
                );

                // 3) Marcar enviado
                outbox.markSent(t.outboxId());
                log.info("[OutboxMatch] SENT requestId={} outboxId={}", t.requestId(), t.outboxId());

            } catch (Exception ex) {
                log.warn("[OutboxMatch] FAIL requestId={} outboxId={} err={}",
                        t.requestId(), t.outboxId(), ex.toString());
                outbox.markFailed(t.outboxId(), ex.getMessage(), backoff, now, maxAttempts);
            }
        }
    }

    private static Optional<String> safeName(String s) {
        if (s == null) return Optional.empty();
        String t = s.trim();
        return t.isEmpty() ? Optional.empty() : Optional.of(t);
    }
}
