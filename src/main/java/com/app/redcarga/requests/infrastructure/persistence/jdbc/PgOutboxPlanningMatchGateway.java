package com.app.redcarga.requests.infrastructure.persistence.jdbc;

import com.app.redcarga.requests.application.internal.gateways.OutboxPlanningMatchGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class PgOutboxPlanningMatchGateway implements OutboxPlanningMatchGateway {

    private final NamedParameterJdbcTemplate jdbc;

    private record Task(
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

    private static final RowMapper<Task> MAPPER = new RowMapper<>() {
        @Override public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Task(
                    rs.getInt("outbox_id"),
                    rs.getInt("request_id"),
                    rs.getString("origin_department_code"),
                    rs.getString("origin_province_code"),
                    rs.getString("dest_department_code"),
                    rs.getString("dest_province_code"),
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("next_attempt_at").toInstant(),
                    rs.getInt("attempt_count"),
                    rs.getString("last_error")
            );
        }
    };

    // Catálogo: requests.outbox_statuses
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_SENT    = 2;
    private static final int STATUS_DEAD    = 3;

    @Override
    public void enqueueMatch(Integer requestId,
                             String originDep, String originProv,
                             String destDep,   String destProv,
                             Instant createdAt,
                             Instant firstAttemptAt) {
        var sql = """
            INSERT INTO requests.outbox_planning_match(
                request_id, origin_department_code, origin_province_code,
                dest_department_code, dest_province_code,
                created_at, next_attempt_at, attempt_count, outbox_status_id
            ) VALUES (:rid, :odep, :oprov, :ddep, :dprov, :cat, :naa, 0, :status)
            ON CONFLICT (request_id) DO NOTHING
            """;
        jdbc.update(sql, Map.of(
                "rid", requestId,
                "odep", originDep,
                "oprov", originProv,
                "ddep", destDep,
                "dprov", destProv,
                "cat", java.util.Date.from(createdAt),
                "naa", java.util.Date.from(firstAttemptAt),
                "status", STATUS_PENDING
        ));
    }

    @Override
    public List<OutboxPlanningMatchGateway.MatchTask> fetchAndLeaseNextBatch(int limit, Duration lease, Instant now) {
        var select = """
        SELECT outbox_id, request_id,
               origin_department_code, origin_province_code,
               dest_department_code,   dest_province_code,
               created_at, next_attempt_at, attempt_count, last_error
          FROM requests.outbox_planning_match
         WHERE outbox_status_id = :pending
           AND (locked_at IS NULL OR locked_at < (:now::timestamptz - make_interval(secs => :lease_secs)))
           AND next_attempt_at <= :now
         ORDER BY next_attempt_at
         LIMIT :limit
        """;

        Map<String, Object> p = new HashMap<>();
        p.put("pending", STATUS_PENDING);
        p.put("now", java.util.Date.from(now));
        p.put("lease_secs", lease.toSeconds());   // <— pasamos segundos, no "30 seconds"
        p.put("limit", limit);

        var tasks = jdbc.query(select, p, MAPPER);
        if (tasks.isEmpty()) return List.of();

        var ids = tasks.stream().map(Task::outboxId).toList();
        var upd = """
        UPDATE requests.outbox_planning_match
           SET locked_at = :now
         WHERE outbox_id IN (:ids)
        """;
        jdbc.update(upd, Map.of("now", java.util.Date.from(now), "ids", ids));

        return tasks.stream()
                .map(t -> new OutboxPlanningMatchGateway.MatchTask(
                        t.outboxId(),
                        t.requestId(),
                        t.originDepartmentCode(),
                        t.originProvinceCode(),
                        t.destDepartmentCode(),
                        t.destProvinceCode(),
                        t.createdAt(),
                        t.nextAttemptAt(),
                        t.attemptCount(),
                        t.lastError()
                ))
                .toList();
    }

    @Override
    public void markSent(Integer outboxId) {
        var sql = """
            UPDATE requests.outbox_planning_match
               SET outbox_status_id = :sent,
                   locked_at = NULL
             WHERE outbox_id = :id
            """;
        jdbc.update(sql, Map.of("sent", STATUS_SENT, "id", outboxId));
    }

    @Override
    public void markFailed(Integer outboxId, String lastError, Duration backoff, Instant now, int maxAttempts) {
        var sql = """
            UPDATE requests.outbox_planning_match
               SET attempt_count = attempt_count + 1,
                   last_error    = :err,
                   next_attempt_at = :next,
                   locked_at = NULL,
                   outbox_status_id = CASE
                       WHEN attempt_count + 1 >= :max THEN :dead
                       ELSE :pending
                   END
             WHERE outbox_id = :id
            """;
        jdbc.update(sql, Map.of(
                "err", lastError,
                "next", java.util.Date.from(now.plus(backoff)),
                "max", maxAttempts,
                "dead", STATUS_DEAD,
                "pending", STATUS_PENDING,
                "id", outboxId
        ));
    }

    @Override
    public void deleteByRequestId(Integer requestId) {
        jdbc.update("DELETE FROM requests.outbox_planning_match WHERE request_id = :rid",
                Map.of("rid", requestId));
    }
}
