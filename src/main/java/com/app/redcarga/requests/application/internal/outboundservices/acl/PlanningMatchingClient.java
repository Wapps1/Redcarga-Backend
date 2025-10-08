package com.app.redcarga.requests.application.internal.outboundservices.acl;

import java.time.Instant;

public interface PlanningMatchingClient {
    void matchAndNotify(
            int requestId,
            String oDep, String oProv,
            String dDep, String dProv,
            java.time.Instant createdAt,
            String requesterNameSnapshot,
            String originDepartmentName,
            String originProvinceName,
            String destDepartmentName,
            String destProvinceName,
            Integer totalQuantity
    );
}
