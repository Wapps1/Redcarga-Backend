package com.app.redcarga.planning.application.internal.acl;

import com.app.redcarga.planning.application.internal.commandservices.RequestInboxCloseService;
import com.app.redcarga.planning.interfaces.acl.PlanningFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanningFacadeImpl implements PlanningFacade {

    private final RequestInboxCloseService closeService;

    @Override
    public void updateRequestInboxStatus(int companyId, int requestId, String newStatus) {
        closeService.updateInboxStatus(companyId, requestId, newStatus);
    }

    @Override
    public void closeAllRequestInboxForRequest(int requestId) {
        closeService.closeInboxForRequest(requestId, false);
    }
}
