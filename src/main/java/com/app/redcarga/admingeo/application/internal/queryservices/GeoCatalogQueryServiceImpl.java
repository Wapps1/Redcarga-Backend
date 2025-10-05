package com.app.redcarga.admingeo.application.internal.queryservices;

import com.app.redcarga.admingeo.domain.model.queries.ExistsDepartmentQuery;
import com.app.redcarga.admingeo.domain.model.queries.ExistsProvinceQuery;
import com.app.redcarga.admingeo.domain.model.queries.ListGeoCatalogQuery;
import com.app.redcarga.admingeo.domain.model.valueobjects.GeoCatalog;
import com.app.redcarga.admingeo.domain.repositories.DepartmentCatalogRepository;
import com.app.redcarga.admingeo.domain.repositories.ProvinceCatalogRepository;
import com.app.redcarga.admingeo.domain.services.GeoCatalogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeoCatalogQueryServiceImpl implements GeoCatalogQueryService {

    private final DepartmentCatalogRepository departments;
    private final ProvinceCatalogRepository provinces;

    @Override
    public GeoCatalog handle(ListGeoCatalogQuery query) {
        var deps  = departments.findAllSortedByName();
        var provs = provinces.findAllSortedByName();
        return new GeoCatalog(deps, provs);
    }

    @Override
    public boolean handle(ExistsDepartmentQuery query) {
        return departments.exists(query.code());
    }

    @Override
    public boolean handle(ExistsProvinceQuery query) {
        return provinces.exists(query.code());
    }
}
