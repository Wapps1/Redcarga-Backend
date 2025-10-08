package com.app.redcarga.planning.domain.queries;
import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import java.util.List;
import java.util.Optional;

public interface ProviderRoutesQueryRepository {
    List<ProviderRouteView> findByCompany(int companyId);

    /** Vista puntual de una ruta, restringida por company. */
    Optional<ProviderRouteView> findByCompanyAndId(int companyId, int routeId);
}