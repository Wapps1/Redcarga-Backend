package com.app.redcarga.planning.application.internal.queryservices;

import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import com.app.redcarga.planning.domain.queries.ProviderRoutesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderRoutesQueryServiceImpl implements ProviderRoutesQueryService {

    private final ProviderRoutesQueryRepository repo;

    @Override
    public List<ProviderRouteView> listRoutes(int companyId) {
        return repo.findByCompany(companyId);
    }

    @Override
    public Optional<ProviderRouteView> findByCompanyAndId(int companyId, int routeId) {
        return repo.findByCompanyAndId(companyId, routeId);
    }
}
