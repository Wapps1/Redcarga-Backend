package com.app.redcarga.requests.application.internal.outboundservices.acl;

public interface PlanningInboxCloseClient {
    void closeInboxForRequest(int requestId, boolean notify);
}
