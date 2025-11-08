package com.app.redcarga.deals.application.internal.outboundservices.notifications;

public interface NotificationsPort {
    void publishNewQuote(NewQuoteNotification notification);
}
