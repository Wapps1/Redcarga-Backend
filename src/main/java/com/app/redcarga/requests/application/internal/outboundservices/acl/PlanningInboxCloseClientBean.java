package com.app.redcarga.requests.application.internal.outboundservices.acl;

import com.app.redcarga.planning.interfaces.acl.PlanningMatchingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("requestsPlanningInboxCloseClientBean")
@RequiredArgsConstructor
public class PlanningInboxCloseClientBean implements PlanningInboxCloseClient {
    private final PlanningMatchingFacade planning;

    @Override
    public void closeInboxForRequest(int requestId, boolean notify) {
        planning.closeInboxForRequest(requestId, notify);
    }
}
