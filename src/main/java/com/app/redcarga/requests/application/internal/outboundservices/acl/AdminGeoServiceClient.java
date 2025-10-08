package com.app.redcarga.requests.application.internal.outboundservices.acl;

import com.app.redcarga.admingeo.interfaces.acl.AdminGeoFacade;
import com.app.redcarga.requests.application.internal.outboundservices.acl.GeoCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("requestsAdminGeoServiceClient")
@RequiredArgsConstructor
public class AdminGeoServiceClient implements GeoCatalogService {
    private final AdminGeoFacade adminGeo;

    @Override public boolean existsDepartmentCode(String departmentCode) { return adminGeo.existsDepartmentCode(departmentCode); }
    @Override public boolean existsProvinceCode(String provinceCode) { return adminGeo.existsProvinceCode(provinceCode); }
}
