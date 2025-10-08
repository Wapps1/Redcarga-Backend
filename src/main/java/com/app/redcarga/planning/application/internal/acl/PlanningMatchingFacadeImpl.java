package com.app.redcarga.planning.application.internal.acl;

import com.app.redcarga.planning.application.internal.commandservices.RequestInboxCloseService;
import com.app.redcarga.planning.application.internal.commandservices.RequestMatchingService;
import com.app.redcarga.planning.interfaces.acl.PlanningMatchingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PlanningMatchingFacadeImpl implements PlanningMatchingFacade {

    private final RequestMatchingService matchingService;
    private final RequestInboxCloseService closeService;

    @Override
    public void matchAndNotify(PlanningMatchingFacade.PlanningMatchCommand cmd) {
        // ORDEN CORRECTO: Dep → Prov, Dep → Prov. Persistimos y notificamos (true).
        matchingService.matchAndNotify(
                cmd.requestId(),
                cmd.originDepartmentCode(),
                cmd.originProvinceCode(),
                cmd.destDepartmentCode(),
                cmd.destProvinceCode(),
                defaulted(cmd.createdAt()),
                true,
                cmd.requesterName(),

                // ===== NUEVO: pasar nombres y totalQuantity =====
                cmd.originDepartmentName(),
                cmd.originProvinceName(),
                cmd.destDepartmentName(),
                cmd.destProvinceName(),
                cmd.totalQuantity()
        );
    }

    @Override
    public void closeInboxForRequest(int requestId, boolean notify) {
        closeService.closeInboxForRequest(requestId, notify);
    }

    private static Instant defaulted(Instant ts) {
        return ts != null ? ts : Instant.now();
    }
}
