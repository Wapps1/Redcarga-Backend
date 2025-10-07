package com.app.redcarga.requests.domain.model.valueobjects;

import lombok.Getter;

import java.util.Map;

@Getter
public enum CloseReasonCode {
    USER_CLOSED(1),
    MATCHED(2),
    TIMEOUT(3),
    CANCELLED(4);

    private final int id;
    CloseReasonCode(int id){ this.id = id; }

    private static final Map<Integer, CloseReasonCode> BY_ID = Map.of(
            1, USER_CLOSED, 2, MATCHED, 3, TIMEOUT, 4, CANCELLED
    );

    public static CloseReasonCode fromId(Integer id) {
        CloseReasonCode r = BY_ID.get(id);
        if (r == null) throw new IllegalArgumentException("close_reason_id_unknown");
        return r;
    }
}
