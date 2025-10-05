package com.app.redcarga.admingeo.application.internal.acl;

import com.app.redcarga.admingeo.domain.model.queries.ExistsDepartmentQuery;
import com.app.redcarga.admingeo.domain.model.queries.ExistsProvinceQuery;
import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceCode;
import com.app.redcarga.admingeo.domain.services.GeoCatalogQueryService;
import com.app.redcarga.admingeo.interfaces.acl.AdminGeoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la fachada (inbound ACL) que traduce strings externos
 * a VOs del dominio y delega en el query service.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminGeoFacadeImpl implements AdminGeoFacade {

    private final GeoCatalogQueryService queries;

    @Override
    public boolean existsDepartmentCode(String departmentCode) {
        var dd = DepartmentCode.of(departmentCode);              // valida y normaliza
        return queries.handle(new ExistsDepartmentQuery(dd));
    }

    @Override
    public boolean existsProvinceCode(String provinceCode) {
        var ddpp = ProvinceCode.of(provinceCode);                // valida y normaliza
        return queries.handle(new ExistsProvinceQuery(ddpp));
    }
}
