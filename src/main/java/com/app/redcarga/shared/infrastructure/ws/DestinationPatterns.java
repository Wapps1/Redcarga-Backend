package com.app.redcarga.shared.infrastructure.ws;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Patrones de destinos WS y helpers de extracción. */
public final class DestinationPatterns {
    private DestinationPatterns() {}

    /** /topic/planning/company.{companyId}.solicitudes */
    public static final Pattern COMPANY_SOLICITUDES =
            Pattern.compile("^/topic/planning/company\\.(\\d+)\\.solicitudes$");

    /** /topic/requests.account.{accountId}.quotes */
    public static final Pattern REQUESTS_ACCOUNT_QUOTES =
            Pattern.compile("^/topic/requests\\.account\\.(\\d+)\\.quotes$");

    public static Integer tryExtractCompanyIdFromCompanySolicitudes(String destination) {
        if (destination == null) return null;
        Matcher m = COMPANY_SOLICITUDES.matcher(destination);
        return m.matches() ? Integer.parseInt(m.group(1)) : null;
    }

    public static Integer tryExtractAccountIdFromRequestsQuotes(String destination) {
        if (destination == null) return null;
        Matcher m = REQUESTS_ACCOUNT_QUOTES.matcher(destination);
        return m.matches() ? Integer.parseInt(m.group(1)) : null;
    }

    /** /topic/deals.quotes.{quoteId}.chat */
    public static final Pattern DEALS_QUOTES_CHAT =
            Pattern.compile("^/topic/deals\\.quotes\\.(\\d+)\\.chat$");

    public static Integer tryExtractQuoteIdFromDealsQuotesChat(String destination) {
        if (destination == null) return null;
        Matcher m = DEALS_QUOTES_CHAT.matcher(destination);
        return m.matches() ? Integer.parseInt(m.group(1)) : null;
    }

    private static final Pattern TRACKING_TOPIC =
            Pattern.compile("^/topic/quotes\\.(\\d+)\\.tracking$");

    public static Optional<Integer> extractQuoteIdFromTrackingTopic(String destination) {
        var m = TRACKING_TOPIC.matcher(destination);
        return m.matches() ? Optional.of(Integer.parseInt(m.group(1))) : Optional.empty();
    }
}
