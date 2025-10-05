package com.app.redcarga.admingeo.domain.repositories;

import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.ProvinceEntry;

import java.util.List;

public interface ProvinceCatalogRepository {

    /** Lista todas las provincias ordenadas por nombre ascendente. */
    List<ProvinceEntry> findAllSortedByName();

    /** Lista provincias de un departamento dado (útil para futuros filtros). */
    List<ProvinceEntry> findByDepartment(DepartmentCode departmentCode);

    /** Verifica existencia exacta del código DDPP. */
    boolean exists(ProvinceCode code);
}
