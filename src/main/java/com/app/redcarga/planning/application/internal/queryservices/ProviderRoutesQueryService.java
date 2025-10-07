package com.app.redcarga.planning.application.internal.queryservices;

import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import java.util.List;

public interface ProviderRoutesQueryService {
    List<ProviderRouteView> listRoutes(int companyId);
}