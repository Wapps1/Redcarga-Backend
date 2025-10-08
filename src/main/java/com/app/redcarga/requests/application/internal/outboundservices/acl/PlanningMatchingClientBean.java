package com.app.redcarga.requests.application.internal.outboundservices.acl;

import com.app.redcarga.planning.interfaces.acl.PlanningMatchingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service("requestsPlanningMatchingClientBean")
@RequiredArgsConstructor
public class PlanningMatchingClientBean implements PlanningMatchingClient {

    private final PlanningMatchingFacade planning;

    @Override
    public void matchAndNotify(int requestId,
                               String oDep, String oProv,
                               String dDep, String dProv,
                               Instant createdAt,
                               String requesterNameSnapshot) {
        var cmd = new PlanningMatchingFacade.PlanningMatchCommand(
                requestId, oDep, dDep, oProv, dProv, createdAt, requesterNameSnapshot
        );
        planning.matchAndNotify(cmd);
    }
}
