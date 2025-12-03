package com.app.redcarga.fleet.application.internal.queryservices;

import com.app.redcarga.fleet.application.internal.outboundservices.acl.IdentityAccountFleetService;
import com.app.redcarga.fleet.application.internal.outboundservices.acl.ProvidersMembershipFleetService;
import com.app.redcarga.fleet.domain.model.aggregates.Driver;
import com.app.redcarga.fleet.domain.model.queries.DriverAccountInfo;
import com.app.redcarga.fleet.domain.repositories.DriverRepository;
import com.app.redcarga.fleet.domain.services.DriverQueryService;
import com.app.redcarga.fleet.interfaces.rest.responses.DriverView;
import com.app.redcarga.identity.interfaces.acl.IdentityPersonSnapshot;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DriverQueryServiceImpl implements DriverQueryService {

    private final DriverRepository drivers;
    private final IdentityAccountFleetService identity;
    private final ProvidersMembershipFleetService providers;
    private static final Logger log = LoggerFactory.getLogger(DriverQueryServiceImpl.class);

    @Override
    public Optional<DriverView> findById(Integer driverId) {
        Optional<Driver> d = drivers.findById(driverId);
        if (d.isEmpty()) return Optional.empty();
        return Optional.of(mapToView(d.get()));
    }

    @Override
    public List<DriverView> findAllByCompany(Integer companyId) {
        List<Driver> ds = drivers.findAllByCompanyId(companyId);
        List<DriverView> out = new ArrayList<>(ds.size());
        for (Driver d : ds) {
            out.add(mapToView(d));
        }
        return out;
    }

    @Override
    public List<DriverView> findAllByCompany(Integer companyId, Integer callerAccountId) {
        // Si se pasó callerAccountId lo validamos (usado por register)
        if (callerAccountId != null) {
            boolean member = providers.isMemberOfCompany(companyId, callerAccountId);
            if (!member) throw new DomainException("caller is not a member of company " + companyId);
        }

        List<Driver> ds = drivers.findAllByCompanyId(companyId);
        List<DriverView> out = new ArrayList<>(ds.size());
        for (Driver d : ds) {
            out.add(mapToView(d));
        }
        return out;
    }

    public boolean existsByIdAndAccountId(Integer accountId, Integer driverId){
        return drivers.existsByIdAndAccountId(accountId, driverId);
    }

    // función simple: por un Driver obtiene optional del ACL por accountId y arma el DriverView
    private DriverView mapToView(Driver d) {
        IdentityPersonSnapshot p = null;
        Integer accountId = d.getAccountId();
        if (accountId != null) {
            p = identity.findByAccountId(accountId).orElse(null);
        }

        String fullName = p != null ? p.fullName() : null;
        String docNumber = p != null ? p.docNumber() : null;
        String phone = p != null ? p.phone() : null;

        return new DriverView(
                d.getId(),
                d.getCompanyId(),
                fullName,
                docNumber,
                phone,
                d.getLicenseNumber(),
                d.isActive(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    @Override
    public Optional<DriverAccountInfo> getDriverAccountInfoByAccountId(Integer accountId) {
        log.debug("DriverQueryService.getDriverAccountInfoByAccountId({})", accountId);
        if (accountId == null) {
            log.debug("accountId is null");
            return Optional.empty();
        }
        Optional<DriverAccountInfo> res = drivers.findByAccountId(accountId)
                .map(d -> new DriverAccountInfo(d.getId(), d.getCompanyId()));
        log.debug("DriverRepository.findByAccountId({}) -> {}", accountId, res);
        return res;
    }
}


