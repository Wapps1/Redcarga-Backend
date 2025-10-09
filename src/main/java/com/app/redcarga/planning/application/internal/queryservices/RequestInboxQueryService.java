package com.app.redcarga.planning.application.internal.queryservices;

import com.app.redcarga.planning.application.internal.views.RequestInboxEntryView;
import java.util.List;

public interface RequestInboxQueryService {
    List<RequestInboxEntryView> findByCompany(int companyId);
}