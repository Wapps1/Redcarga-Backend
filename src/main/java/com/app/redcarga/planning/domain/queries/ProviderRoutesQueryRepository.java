package com.app.redcarga.planning.domain.queries;
import com.app.redcarga.planning.application.internal.views.ProviderRouteView;
import java.util.List;

public interface ProviderRoutesQueryRepository {
    List<ProviderRouteView> findByCompany(
            int companyId,
            String shape,                 // "DD" | "PP" | null
            Boolean active,               // true | false | null
            String originDepartmentCode,  // p.ej. "15" (Lima) | null
            String originProvinceCode,    // p.ej. "1501"       | null
            String destDepartmentCode,    // p.ej. "04"         | null
            String destProvinceCode       // p.ej. "0401"       | null
    );
}