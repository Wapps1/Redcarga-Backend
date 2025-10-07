package com.app.redcarga.planning.application.internal.queryservices;

import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import com.app.redcarga.planning.domain.queries.ProviderRoutesQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProviderRoutesQueryServiceImpl implements ProviderRoutesQueryService {

    private final ProviderRoutesQueryRepository repo;

    @Override
    public List<ProviderRouteView> listRoutes(
            int companyId,
            String shape,
            Boolean active,
            String originDepartmentCode,
            String originProvinceCode,
            String destDepartmentCode,
            String destProvinceCode
    ) {
        return repo.findByCompany(companyId, shape, active, originDepartmentCode, originProvinceCode, destDepartmentCode, destProvinceCode);
    }
}
