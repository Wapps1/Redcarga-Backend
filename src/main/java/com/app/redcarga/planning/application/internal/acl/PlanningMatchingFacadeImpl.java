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
        matchingService.matchAndNotify(
                cmd.requestId(),
                cmd.originProvinceCode(),
                cmd.originDepartmentCode(),
                cmd.destProvinceCode(),
                cmd.destDepartmentCode(),
                defaulted(cmd.createdAt()),
                false,                       // siempre persistimos y notificamos en flujos reales
                cmd.requesterName()
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
