package com.app.redcarga.shared.infrastructure.ws;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Patrones de destinos WS y helpers de extracción. */
public final class DestinationPatterns {
    private DestinationPatterns() {}

    /** /topic/planning/company.{companyId}.solicitudes */
    public static final Pattern COMPANY_SOLICITUDES =
            Pattern.compile("^/topic/planning/company\\.(\\d+)\\.solicitudes$");

    public static Integer tryExtractCompanyIdFromCompanySolicitudes(String destination) {
        if (destination == null) return null;
        Matcher m = COMPANY_SOLICITUDES.matcher(destination);
        return m.matches() ? Integer.parseInt(m.group(1)) : null;
    }
}
