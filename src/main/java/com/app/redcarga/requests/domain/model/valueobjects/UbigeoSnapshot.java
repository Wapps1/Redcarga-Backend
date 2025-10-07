package com.app.redcarga.requests.domain.model.valueobjects;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class UbigeoSnapshot {

    @Embedded
    private final DepartmentCode departmentCode;

    @Column(name = "department_name", length = 100, nullable = false)
    private final String departmentName;

    @Embedded
    private final ProvinceCode provinceCode;   // puede ser null

    @Column(name = "province_name", length = 100)
    private final String provinceName;

    @Embedded
    private final DistrictText districtText;   // texto libre UI

    protected UbigeoSnapshot() { // JPA
        this.departmentCode = null;
        this.departmentName = null;
        this.provinceCode   = null;
        this.provinceName   = null;
        this.districtText   = null;
    }

    private UbigeoSnapshot(DepartmentCode dep, String depName,
                           ProvinceCode prov, String provName,
                           DistrictText distText) {
        this.departmentCode = dep;
        this.departmentName = requireName(depName, 1, 100, "department_name_invalid");
        this.provinceCode   = prov;
        this.provinceName   = provName == null ? null : requireName(provName, 1, 100, "province_name_invalid");
        this.districtText   = distText;
    }

    public static UbigeoSnapshot of(DepartmentCode dep, String depName,
                                    ProvinceCode prov, String provName,
                                    DistrictText districtText) {
        if (dep == null) throw new IllegalArgumentException("department_code_required");
        if (depName == null) throw new IllegalArgumentException("department_name_required");
        return new UbigeoSnapshot(dep, depName, prov, provName, districtText);
    }

    private static String requireName(String raw, int min, int max, String code) {
        String t = raw == null ? null : raw.trim();
        if (t == null || t.length() < min || t.length() > max) throw new IllegalArgumentException(code);
        return t;
    }
}
