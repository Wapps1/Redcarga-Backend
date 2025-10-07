package com.app.redcarga.fleet.application.internal.commandservices;

import com.app.redcarga.fleet.application.internal.outboundservices.acl.CompaniesCatalogService;
import com.app.redcarga.fleet.domain.model.aggregates.Vehicle;
import com.app.redcarga.fleet.domain.model.commands.CreateVehicleCommand;
import com.app.redcarga.fleet.domain.model.commands.DeleteVehicleCommand;
import com.app.redcarga.fleet.domain.model.commands.UpdateVehicleCommand;
import com.app.redcarga.fleet.domain.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleCommandServiceImpl implements com.app.redcarga.fleet.domain.services.VehicleCommandService {

    private final VehicleRepository vehicles;
    private final CompaniesCatalogService companies;

    @Override
    @Transactional
    public Integer handle(CreateVehicleCommand cmd) {
        validateCompanyExists(cmd.companyId());
        if (vehicles.existsByCompanyIdAndPlate(cmd.companyId(), cmd.plate()))
            throw new IllegalStateException("vehicle_plate_conflict");

        var v = Vehicle.create(cmd.companyId(), cmd.name(), cmd.plate(), cmd.active());
        vehicles.save(v);
        return v.getId();
    }

    @Override
    @Transactional
    public void handle(UpdateVehicleCommand cmd) {
        var v = vehicles.findById(cmd.vehicleId()).orElseThrow(() -> new IllegalArgumentException("vehicle_not_found"));
        v.update(cmd.name(), cmd.plate(), cmd.active());
        vehicles.save(v);
    }

    @Override
    @Transactional
    public void handle(DeleteVehicleCommand cmd) {
        var v = vehicles.findById(cmd.vehicleId()).orElseThrow(() -> new IllegalArgumentException("vehicle_not_found"));
        vehicles.delete(v);
    }

    private void validateCompanyExists(Integer companyId) {
        if (companyId == null || companyId <= 0) throw new IllegalArgumentException("companyId_invalid");
        if (!companies.existsCompany(companyId)) throw new IllegalArgumentException("company_not_found");
    }
}


