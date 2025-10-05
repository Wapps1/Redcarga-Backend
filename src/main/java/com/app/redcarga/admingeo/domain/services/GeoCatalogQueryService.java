package com.app.redcarga.admingeo.domain.services;

import com.app.redcarga.admingeo.domain.model.valueobjects.*;
import com.app.redcarga.admingeo.domain.model.queries.ExistsDepartmentQuery;
import com.app.redcarga.admingeo.domain.model.queries.ExistsProvinceQuery;
import com.app.redcarga.admingeo.domain.model.queries.ListGeoCatalogQuery;

/**
 * Interfaz de caso de uso (solo lectura) para AdminGeo.
 * La implementación vive en application.internal.queryservices.
 */
public interface GeoCatalogQueryService {

    /** Resuelve el catálogo completo (departments + provinces). */
    GeoCatalog handle(ListGeoCatalogQuery query);

    /** Confirma existencia de un código de departamento (DD). */
    boolean handle(ExistsDepartmentQuery query);

    /** Confirma existencia de un código de provincia (DDPP). */
    boolean handle(ExistsProvinceQuery query);
}
