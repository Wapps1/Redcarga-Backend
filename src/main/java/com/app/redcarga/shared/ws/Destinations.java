package com.app.redcarga.shared.ws;

public final class Destinations {
    private Destinations() {}

    /** Broadcast por empresa: nuevas solicitudes de Planning. */
    public static final String TOPIC_PLANNING_COMPANY = "/topic/planning/company.%d.solicitudes";

    /** Cola personal del usuario autenticado para Planning. */
    public static final String USER_QUEUE_PLANNING = "/user/queue/planning/solicitudes";

    /** Topic para que el requester reciba cotizaciones de sus requests. */
    public static final String TOPIC_REQUEST_ACCOUNT_QUOTES = "/topic/requests.account.%d.quotes";

    /** Topic de chat por quote en Deals. */
    public static final String TOPIC_DEALS_QUOTES_CHAT = "/topic/deals.quotes.%d.chat";

    public static final String TOPIC_QUOTE_TRACKING_TEMPLATE = "/topic/quotes.%d.tracking";

}
