package com.app.redcarga.requests.infrastructure.persistence.jpa.repositories;

import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import com.app.redcarga.requests.domain.repositories.RequestNameUbigeoRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRequestRepository
        extends JpaRepository<Request, Integer>, RequestRepository {
    @Override
    List<Request> findAllByRequesterAccountId(Integer requesterAccountId);

    @Override
    @Query("""
        select distinct r
        from Request r
        left join fetch r.items it
        where r.id = :id
    """)
    Optional<Request> findByIdWithItemsAndImages(@Param("id") Integer id);

    // Proyección ligera que devuelve request name + campos individuales de origin/destination
    @Query("select new com.app.redcarga.requests.domain.repositories.RequestNameUbigeoRaw(" +
            "r.id, r.requestName, " +
            "r.origin.departmentCode.value, r.origin.departmentName, r.origin.provinceCode.value, r.origin.provinceName, r.origin.districtText.value, " +
            "r.destination.departmentCode.value, r.destination.departmentName, r.destination.provinceCode.value, r.destination.provinceName, r.destination.districtText.value) " +
            "from Request r where r.id = :id")
    Optional<RequestNameUbigeoRaw> findRequestNameById(@Param("id") Integer id);
}
