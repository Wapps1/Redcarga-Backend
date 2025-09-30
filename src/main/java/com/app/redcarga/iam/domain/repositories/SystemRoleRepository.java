package com.app.redcarga.iam.domain.repositories;

import java.util.Optional;

public interface SystemRoleRepository {
    /** Devuelve el PK de iam.system_roles dado el code (case-insensitive). */
    Optional<Integer> findIdByCode(String code);

    /** Devuelve el code dado el PK. Útil si luego quieres mostrarlo en /bootstrap. */
    Optional<String> findCodeById(Integer id);

    /** ¿Existe el code? (case-insensitive). */
    boolean existsByCode(String code);
}
