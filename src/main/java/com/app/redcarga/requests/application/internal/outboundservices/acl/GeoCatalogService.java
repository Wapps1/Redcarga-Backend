package com.app.redcarga.requests.application.internal.outboundservices.acl;

public interface GeoCatalogService {
    boolean existsDepartmentCode(String departmentCode);
    boolean existsProvinceCode(String provinceCode);
}
