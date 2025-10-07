package com.app.redcarga.requests.domain.model.valueobjects;

import lombok.Getter;

import java.util.Map;

@Getter
public enum RequestStatusCode {
    OPEN(1),
    CLOSED(2),
    CANCELLED(3),
    EXPIRED(4);

    private final int id;
    RequestStatusCode(int id){ this.id = id; }

    private static final Map<Integer, RequestStatusCode> BY_ID = Map.of(
            1, OPEN, 2, CLOSED, 3, CANCELLED, 4, EXPIRED
    );

    public static RequestStatusCode fromId(Integer id) {
        RequestStatusCode s = BY_ID.get(id);
        if (s == null) throw new IllegalArgumentException("request_status_id_unknown");
        return s;
    }

    public static RequestStatusCode require(RequestStatusCode s) {
        if (s == null) throw new IllegalArgumentException("request_status_required");
        return s;
    }

    /** Reglas de transición (dominio) */
    public boolean canTransitionTo(RequestStatusCode target) {
        return switch (this) {
            case OPEN -> (target == CLOSED || target == CANCELLED || target == EXPIRED);
            case CLOSED, CANCELLED, EXPIRED -> false;
        };
    }
}
