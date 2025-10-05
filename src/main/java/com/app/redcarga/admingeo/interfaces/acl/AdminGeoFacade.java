package com.app.redcarga.admingeo.interfaces.acl;

/**
 * Inbound ACL de AdminGeo para otros bounded contexts.
 * Contrato estable y mínimo: verificación de existencia UBIGEO.
 */
public interface AdminGeoFacade {

    /**
     * Confirma si existe un departamento con código UBIGEO de 2 dígitos (DD).
     * @param departmentCode string tipo "01".."25" (se normaliza; ej. "1" -> "01")
     * @return true si existe; false en caso contrario
     * @throws IllegalArgumentException si el código es inválido
     */
    boolean existsDepartmentCode(String departmentCode);

    /**
     * Confirma si existe una provincia con código UBIGEO de 4 dígitos (DDPP).
     * @param provinceCode string tipo "0101".."2501" (se normaliza; ej. "101" -> "0101")
     * @return true si existe; false en caso contrario
     * @throws IllegalArgumentException si el código es inválido
     */
    boolean existsProvinceCode(String provinceCode);
}
