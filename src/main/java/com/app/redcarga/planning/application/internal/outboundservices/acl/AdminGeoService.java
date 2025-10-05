package com.app.redcarga.planning.application.internal.outboundservices.acl;

/** Cliente interno tipado para hablar con AdminGeo (a través de su inbound facade). */
public interface AdminGeoService {
    boolean existsDepartmentCode(String departmentCode); // normaliza en el otro BC
    boolean existsProvinceCode(String provinceCode);     // normaliza en el otro BC
}
