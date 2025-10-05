package com.app.redcarga.planning.application.internal.outboundservices.acl;

import com.app.redcarga.admingeo.interfaces.acl.AdminGeoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Implementación que delega al inbound ACL del BC AdminGeo. */
@Service
@RequiredArgsConstructor
public class AdminGeoServiceClient implements AdminGeoService {

    private final AdminGeoFacade adminGeo;

    @Override
    public boolean existsDepartmentCode(String departmentCode) {
        return adminGeo.existsDepartmentCode(departmentCode);
    }

    @Override
    public boolean existsProvinceCode(String provinceCode) {
        return adminGeo.existsProvinceCode(provinceCode);
    }
}
