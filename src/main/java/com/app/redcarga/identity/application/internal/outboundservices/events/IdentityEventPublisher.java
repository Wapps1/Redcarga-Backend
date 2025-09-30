package com.app.redcarga.identity.application.internal.outboundservices.events;

public interface IdentityEventPublisher {
    void publish(Object event);
}
