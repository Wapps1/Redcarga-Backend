package com.app.redcarga.fleet.domain.services;

import com.app.redcarga.fleet.domain.model.commands.CreateVehicleCommand;
import com.app.redcarga.fleet.domain.model.commands.UpdateVehicleCommand;
import com.app.redcarga.fleet.domain.model.commands.DeleteVehicleCommand;

public interface VehicleCommandService {
    Integer handle(CreateVehicleCommand cmd);
    void handle(UpdateVehicleCommand cmd);
    void handle(DeleteVehicleCommand cmd);
}


