package com.app.redcarga.fleet.interfaces.rest;

import com.app.redcarga.fleet.domain.model.aggregates.Vehicle;
import com.app.redcarga.fleet.domain.model.commands.CreateVehicleCommand;
import com.app.redcarga.fleet.domain.model.commands.DeleteVehicleCommand;
import com.app.redcarga.fleet.domain.model.commands.UpdateVehicleCommand;
import com.app.redcarga.fleet.domain.services.VehicleCommandService;
import com.app.redcarga.fleet.domain.services.VehicleQueryService;
import com.app.redcarga.fleet.interfaces.rest.requests.RegisterVehicleRequest;
import com.app.redcarga.fleet.interfaces.rest.requests.UpdateVehicleRequest;
import com.app.redcarga.fleet.interfaces.rest.responses.RegisterVehicleResponse;
import com.app.redcarga.fleet.interfaces.rest.responses.VehicleView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fleet")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class VehiclesController {

    private final VehicleCommandService vehicleCommands;
    private final VehicleQueryService vehicleQueries;

    @PostMapping("/companies/{companyId}/vehicles")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Register a vehicle for the given company")
    public ResponseEntity<RegisterVehicleResponse> register(
            @PathVariable int companyId,
            @Valid @RequestBody RegisterVehicleRequest body
    ) {
        var cmd = new CreateVehicleCommand(
                companyId,
                body.name(),
                body.plate(),
                body.active()
        );
        Integer id = vehicleCommands.handle(cmd);
        return ResponseEntity.ok(new RegisterVehicleResponse(true, id));
    }

    @GetMapping("/companies/{companyId}/vehicles")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "List vehicles by company")
    public ResponseEntity<List<VehicleView>> listByCompany(@PathVariable int companyId) {
        List<VehicleView> items = vehicleQueries.findAllByCompany(companyId)
                .stream().map(VehiclesController::toView).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/vehicles/{vehicleId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Get vehicle by id")
    public ResponseEntity<VehicleView> getById(@PathVariable int vehicleId) {
        return vehicleQueries.findById(vehicleId)
                .map(VehiclesController::toView)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/vehicles/{vehicleId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update a vehicle")
    public ResponseEntity<Void> update(
            @PathVariable int vehicleId,
            @Valid @RequestBody UpdateVehicleRequest body
    ) {
        var cmd = new UpdateVehicleCommand(
                vehicleId,
                body.name(),
                body.plate(),
                body.active()
        );
        vehicleCommands.handle(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Delete a vehicle")
    public ResponseEntity<Void> delete(@PathVariable int vehicleId) {
        vehicleCommands.handle(new DeleteVehicleCommand(vehicleId));
        return ResponseEntity.noContent().build();
    }

    private static VehicleView toView(Vehicle v) {
        return new VehicleView(
                v.getId(),
                v.getCompanyId(),
                v.getName(),
                v.getPlate(),
                v.isActive(),
                v.getCreatedAt() != null ? v.getCreatedAt().getTime() : null,
                v.getUpdatedAt() != null ? v.getUpdatedAt().getTime() : null
        );
    }
}


