package com.app.redcarga.admingeo.domain.repositories;

import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentCode;
import com.app.redcarga.admingeo.domain.model.valueobjects.DepartmentEntry;

import java.util.List;

public interface DepartmentCatalogRepository {

    /** Lista todos los departamentos ordenados por nombre ascendente. */
    List<DepartmentEntry> findAllSortedByName();

    /** Verifica existencia exacta del código DD. */
    boolean exists(DepartmentCode code);
}
