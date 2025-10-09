package com.app.redcarga.planning.application.internal.gateways;

import com.app.redcarga.planning.application.internal.views.RequestInboxEntryView;
import java.util.List;

public interface RequestInboxQueryRepository {
    List<RequestInboxEntryView> findByCompany(int companyId);
}