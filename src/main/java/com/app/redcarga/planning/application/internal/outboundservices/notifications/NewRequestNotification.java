package com.app.redcarga.planning.application.internal.outboundservices.notifications;

import java.time.Instant;

/**
 * Payload enviado al tópico de la compañía cuando nace una solicitud que les aplica.
 * Suficientemente auto-contenido para UI (sin hacer otra consulta).
 */
public record NewRequestNotification(
        String type,                 // "NEW_REQUEST"
        int requestId,
        int companyId,
        int routeId,
        int routeTypeId,            // 1=DD, 2=PP (según catálogo)
        String matchKind,           // "DD" | "PP" (derivado del catálogo)
        String originDepartmentCode,
        String originProvinceCode,  // puede ser null en DD
        String destDepartmentCode,
        String destProvinceCode,    // puede ser null en DD
        Instant createdAt,
        String requesterName,

        String originDepartmentName,
        String originProvinceName,
        String destDepartmentName,
        String destProvinceName,

        Integer totalQuantity
) {
    public static NewRequestNotification of(String matchKind,
                                            int requestId,
                                            int companyId,
                                            int routeId,
                                            int routeTypeId,
                                            String oDep, String oProv,
                                            String dDep, String dProv,
                                            Instant createdAt, String requesterName,
                                            String originDepartmentName,
                                            String originProvinceName,
                                            String destDepartmentName,
                                            String destProvinceName,
                                            Integer totalQuantity) {
        return new NewRequestNotification("NEW_REQUEST", requestId, companyId, routeId, routeTypeId,
                matchKind, oDep, oProv, dDep, dProv, createdAt,requesterName,originDepartmentName,
                originProvinceName,destDepartmentName, destProvinceName, totalQuantity);
    }
}
