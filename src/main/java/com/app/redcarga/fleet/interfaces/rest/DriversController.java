package com.app.redcarga.fleet.interfaces.rest;

import com.app.redcarga.fleet.domain.model.aggregates.Driver;
import com.app.redcarga.fleet.domain.model.commands.CreateDriverCommand;
import com.app.redcarga.fleet.domain.model.commands.DeleteDriverCommand;
import com.app.redcarga.fleet.domain.model.commands.UpdateDriverCommand;
import com.app.redcarga.fleet.domain.services.DriverCommandService;
import com.app.redcarga.fleet.domain.services.DriverQueryService;
import com.app.redcarga.fleet.interfaces.rest.requests.RegisterDriverRequest;
import com.app.redcarga.fleet.interfaces.rest.requests.UpdateDriverRequest;
import com.app.redcarga.fleet.interfaces.rest.responses.RegisterDriverResponse;
import com.app.redcarga.fleet.interfaces.rest.responses.DriverView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fleet")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class DriversController {

    private final DriverCommandService driverCommands;
    private final DriverQueryService driverQueries;

    @PostMapping("/companies/{companyId}/drivers")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Register a driver for the given company")
    public ResponseEntity<RegisterDriverResponse> register(
            @PathVariable int companyId,
            @Valid @RequestBody RegisterDriverRequest body,
            JwtAuthenticationToken principal
    ) {
        // extraer account id del caller desde el token (subject)
        Integer callerAccountId = null;
        try {
            callerAccountId = Integer.valueOf(principal.getToken().getSubject());
        } catch (Exception e) {
            // si no se puede extraer el subject, denegar
            return ResponseEntity.status(403).body(new RegisterDriverResponse(false, null));
        }

        // usar la nueva sobrecarga para validar que el caller pertenece a la company
        driverQueries.findAllByCompany(companyId, callerAccountId); // lanzará AccessDenied si no pertenece

        var cmd = new CreateDriverCommand(
                companyId,
                body.accountId(),
                body.licenseNumber(),
                body.active()
        );
        Integer id = driverCommands.handle(cmd);
        return ResponseEntity.ok(new RegisterDriverResponse(true, id));
    }

    @GetMapping("/companies/{companyId}/drivers")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "List drivers by company")
    public ResponseEntity<List<DriverView>> listByCompany(@PathVariable int companyId, JwtAuthenticationToken principal) {

        Integer callerAccountId = Integer.valueOf(principal.getToken().getSubject());

        List<DriverView> items = driverQueries.findAllByCompany(companyId, callerAccountId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/drivers/{driverId}")
    @Operation(summary = "Get driver by id")
    public ResponseEntity<DriverView> getById(@PathVariable int driverId, JwtAuthenticationToken principal) {
        return driverQueries.findById(driverId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/drivers/{driverId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update a driver") //falta proteger para q solo un miembro autorizado de la compañia pueda
    public ResponseEntity<Void> update(
            @PathVariable int driverId,
            @Valid @RequestBody UpdateDriverRequest body
    ) {
        var cmd = new UpdateDriverCommand(
                driverId,
                body.licenseNumber(),
                body.active()
        );
        driverCommands.handle(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/drivers/{driverId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Delete a driver")
    public ResponseEntity<Void> delete(@PathVariable int driverId) {
        driverCommands.handle(new DeleteDriverCommand(driverId));
        return ResponseEntity.noContent().build();
    }
}


