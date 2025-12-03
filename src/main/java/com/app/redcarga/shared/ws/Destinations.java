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

    /** Topic de tracking por quote. */
    public static final String TOPIC_QUOTE_TRACKING_TEMPLATE = "/topic/quotes.%d.tracking";

    /** App destination para driver enviando ubicación. */
    public static final String APP_TRACKING_UPDATE_TEMPLATE = "/app/quotes.%d.tracking.update";

    public static String topicQuoteTracking(int quoteId) {
        return String.format(TOPIC_QUOTE_TRACKING_TEMPLATE, quoteId);
    }

    public static String appTrackingUpdate(int quoteId) {
        return String.format(APP_TRACKING_UPDATE_TEMPLATE, quoteId);
    }

}
