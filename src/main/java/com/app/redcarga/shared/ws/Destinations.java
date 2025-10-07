package com.app.redcarga.shared.ws;

public final class Destinations {
    private Destinations() {}

    /** Broadcast por empresa: nuevas solicitudes de Planning. */
    public static final String TOPIC_PLANNING_COMPANY = "/topic/planning/company.%d.solicitudes";

    /** Cola personal del usuario autenticado para Planning. */
    public static final String USER_QUEUE_PLANNING = "/user/queue/planning/solicitudes";
}
