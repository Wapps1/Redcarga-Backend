package com.app.redcarga.planning.domain.queries;
import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import java.util.List;

public interface ProviderRoutesQueryRepository {
    List<ProviderRouteView> findByCompany(int companyId);
}