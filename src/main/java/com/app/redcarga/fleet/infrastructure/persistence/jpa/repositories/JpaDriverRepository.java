package com.app.redcarga.fleet.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.fleet.domain.model.aggregates.Driver;
import com.app.redcarga.fleet.domain.repositories.DriverRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaDriverRepository extends JpaRepository<Driver, Integer>, DriverRepository {

    @Override
    Optional<Driver> findById(Integer id);

    @Override
    boolean existsByCompanyIdAndLicenseNumber(Integer companyId, String licenseNumber);

    @Override
    List<Driver> findAllByCompanyId(Integer companyId);

    // Forzar JPQL: asume que el atributo en Driver entity se llama 'accountId'
    // Si en tu entity el atributo se llama distinto (ej. 'ownerAccountId'), cámbialo aquí
    @Override
    @Query("SELECT d FROM Driver d WHERE d.accountId = :accountId")
    Optional<Driver> findByAccountId(@Param("accountId") Integer accountId);

    @Override
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Driver d WHERE d.id = :driverId AND d.accountId = :accountId")
    boolean existsByIdAndAccountId(@Param("accountId") Integer accountId, @Param("driverId") Integer driverId);
}


