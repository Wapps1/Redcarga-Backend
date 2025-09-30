package com.app.redcarga.identity.domain.repositories;

import java.util.Optional;

public interface DocTypeCatalogRepository {
    Optional<Integer> findIdByCode(String code);
    Optional<String> findCodeById(Integer id);
    boolean existsCode(String code);
}
