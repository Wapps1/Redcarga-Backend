package com.app.redcarga.fleet.application.internal.commandservices;

import com.app.redcarga.fleet.application.internal.outboundservices.acl.CompaniesCatalogService;
import com.app.redcarga.fleet.domain.model.aggregates.Driver;
import com.app.redcarga.fleet.domain.model.commands.CreateDriverCommand;
import com.app.redcarga.fleet.domain.model.commands.DeleteDriverCommand;
import com.app.redcarga.fleet.domain.model.commands.UpdateDriverCommand;
import com.app.redcarga.fleet.domain.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverCommandServiceImpl implements com.app.redcarga.fleet.domain.services.DriverCommandService {

    private final DriverRepository drivers;
    private final CompaniesCatalogService companies;

    @Override
    @Transactional
    public Integer handle(CreateDriverCommand cmd) {
        validateCompanyExists(cmd.companyId());
        if (cmd.licenseNumber() != null && !cmd.licenseNumber().isBlank()) {
            if (drivers.existsByCompanyIdAndLicenseNumber(cmd.companyId(), cmd.licenseNumber()))
                throw new IllegalStateException("driver_license_conflict");
        }
        var d = Driver.create(cmd.companyId(), cmd.firstName(), cmd.lastName(), cmd.email(), cmd.phone(), cmd.licenseNumber(), cmd.active());
        drivers.save(d);
        return d.getId();
    }

    @Override
    @Transactional
    public void handle(UpdateDriverCommand cmd) {
        var d = drivers.findById(cmd.driverId()).orElseThrow(() -> new IllegalArgumentException("driver_not_found"));
        d.update(cmd.firstName(), cmd.lastName(), cmd.email(), cmd.phone(), cmd.licenseNumber(), cmd.active());
        drivers.save(d);
    }

    @Override
    @Transactional
    public void handle(DeleteDriverCommand cmd) {
        var d = drivers.findById(cmd.driverId()).orElseThrow(() -> new IllegalArgumentException("driver_not_found"));
        drivers.delete(d);
    }

    private void validateCompanyExists(Integer companyId) {
        if (companyId == null || companyId <= 0) throw new IllegalArgumentException("companyId_invalid");
        if (!companies.existsCompany(companyId)) throw new IllegalArgumentException("company_not_found");
    }
}


