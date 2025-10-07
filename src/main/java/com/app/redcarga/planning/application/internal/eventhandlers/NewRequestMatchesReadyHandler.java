package com.app.redcarga.planning.application.internal.eventhandlers;

import com.app.redcarga.planning.application.internal.events.NewRequestMatchesReady;
import com.app.redcarga.planning.application.internal.gateways.RouteTypeCatalogGateway;
import com.app.redcarga.planning.application.internal.outboundservices.notifications.NewRequestNotification;
import com.app.redcarga.planning.application.internal.outboundservices.notifications.NotificationsPort;
import com.app.redcarga.planning.domain.model.valueobjects.RouteShape;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewRequestMatchesReadyHandler {

    private final NotificationsPort notifications;
    private final RouteTypeCatalogGateway routeTypes; // ya lo tienes en app.internal.gateways

    @EventListener
    public void on(NewRequestMatchesReady e) {
        // En este punto el evento ya fue publicado AFTER_COMMIT por el publisher de infra.
        for (var m : e.matches()) {
            RouteShape shape = routeTypes.requireShapeByRouteTypeId(m.routeTypeId());
            String matchKind = shape.name(); // "PP" | "DD"

            var n = NewRequestNotification.of(
                    matchKind,
                    e.requestId(),
                    m.companyId(),
                    m.routeId(),
                    m.routeTypeId(),
                    e.originDepartmentCode(),
                    e.originProvinceCode(),
                    e.destDepartmentCode(),
                    e.destProvinceCode(),
                    e.createdAt(),
                    e.requesterName()
            );

            notifications.notifyNewRequest(n);
        }
    }
}
