package com.app.redcarga.planning.application.internal.queryservices;

import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import java.util.List;
import java.util.Optional;

public interface ProviderRoutesQueryService {
    List<ProviderRouteView> listRoutes(int companyId);

    Optional<ProviderRouteView> findByCompanyAndId(int companyId, int routeId);
}