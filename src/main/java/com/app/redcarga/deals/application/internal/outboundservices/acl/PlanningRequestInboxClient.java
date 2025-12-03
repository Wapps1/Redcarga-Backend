package com.app.redcarga.deals.application.internal.outboundservices.acl;

import com.app.redcarga.planning.interfaces.acl.PlanningFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Outbound ACL client: deals BC consumes planning BC inbox facade.
 */
@Service
@RequiredArgsConstructor
public class PlanningRequestInboxClient {

	private final PlanningFacade planningFacade;

	public void updateRequestInboxStatus(int companyId, int requestId, String newStatus) {
		planningFacade.updateRequestInboxStatus(companyId, requestId, newStatus);
	}

	public void closeAllRequestInboxForRequest(int requestId) {
		planningFacade.closeAllRequestInboxForRequest(requestId);
	}
}
