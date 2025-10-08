package com.app.redcarga.planning.application.internal.commandservices;

import com.app.redcarga.planning.application.internal.gateways.RouteTypeCatalogGateway;
import com.app.redcarga.planning.application.internal.outboundservices.acl.AdminGeoService;
import com.app.redcarga.planning.application.internal.outboundservices.acl.ProvidersMembershipService;
import com.app.redcarga.planning.domain.model.aggregates.ProviderRoute;
import com.app.redcarga.planning.domain.model.commands.RegisterProviderRouteCommand;
import com.app.redcarga.planning.domain.model.commands.UpdateProviderRouteCommand;
import com.app.redcarga.planning.domain.model.commands.DeleteProviderRouteCommand;
import com.app.redcarga.planning.domain.model.valueobjects.RouteShape;
import com.app.redcarga.planning.domain.repositories.ProviderRouteRepository;
import com.app.redcarga.planning.domain.services.ProviderRouteCommandService;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Variante que incluye autorización ADMIN por compañía dentro del caso de uso. */
@Service
@RequiredArgsConstructor
public class ProviderRouteCommandServiceImpl implements ProviderRouteCommandService {

    private final ProviderRouteRepository routes;
    private final RouteTypeCatalogGateway routeTypes;
    private final AdminGeoService adminGeo;
    private final ProvidersMembershipService memberships;

    @Override
    @Transactional
    public Integer register(RegisterProviderRouteCommand cmd, int actorAccountId) {
        // A) Autorización
        boolean ok = memberships.hasAnyRole(cmd.companyId(), actorAccountId, List.of("ADMIN"));
        if (!ok) throw new DomainException("forbidden_company_admin_required");

        // B) Igual que el service base
        RouteShape shape = routeTypes.requireShapeByRouteTypeId(cmd.routeTypeId());
        requireDepartmentExists(cmd.originDepartmentCode(), "geo_department_not_found");
        requireDepartmentExists(cmd.destDepartmentCode(),   "geo_department_not_found");
        if (shape == RouteShape.PP) {
            requireProvinceExists(cmd.originProvinceCode(), "geo_province_not_found");
            requireProvinceExists(cmd.destProvinceCode(),   "geo_province_not_found");
        }

        if (shape == RouteShape.DD) {
            if (routes.existsDD(cmd.companyId(), cmd.originDepartmentCode(), cmd.destDepartmentCode()))
                throw new DomainException("route_already_exists");
            var route = ProviderRoute.createDD(
                    cmd.companyId(), cmd.routeTypeId(),
                    cmd.originDepartmentCode(), cmd.destDepartmentCode(),
                    cmd.active());
            routes.save(route);
            return route.getId();
        } else {
            if (routes.existsPP(cmd.companyId(), cmd.originProvinceCode(), cmd.destProvinceCode()))
                throw new DomainException("route_already_exists");
            var route = ProviderRoute.createPP(
                    cmd.companyId(), cmd.routeTypeId(),
                    cmd.originDepartmentCode(), cmd.destDepartmentCode(),
                    cmd.originProvinceCode(), cmd.destProvinceCode(),
                    cmd.active());
            routes.save(route);
            return route.getId();
        }
    }

    @Override
    @Transactional
    public void update(UpdateProviderRouteCommand cmd, int actorAccountId) {
        // A) Autorización (ADMIN de la company)
        boolean ok = memberships.hasAnyRole(cmd.companyId(), actorAccountId, List.of("ADMIN"));
        if (!ok) throw new DomainException("forbidden_company_admin_required");

        var route = routes.findById(cmd.routeId())
                .orElseThrow(() -> new DomainException("route_not_found"));
        if (!route.getCompanyId().equals(cmd.companyId()))
            throw new DomainException("forbidden_different_company");

        RouteShape shape = routeTypes.requireShapeByRouteTypeId(cmd.routeTypeId());

        // Validaciones de catálogo geo
        requireDepartmentExists(cmd.originDepartmentCode(), "geo_department_not_found");
        requireDepartmentExists(cmd.destDepartmentCode(),   "geo_department_not_found");
        if (shape == RouteShape.PP) {
            requireProvinceExists(cmd.originProvinceCode(), "geo_province_not_found");
            requireProvinceExists(cmd.destProvinceCode(),   "geo_province_not_found");
        }

        if (shape == RouteShape.DD) {
            // Chequeo de duplicado si cambian claves DD o el shape anterior no era DD
            boolean sameDDKeys = route.getOriginProvince() == null && route.getDestProvince() == null
                    && route.getOriginDepartment().getValue().equals(cmd.originDepartmentCode())
                    && route.getDestDepartment().getValue().equals(cmd.destDepartmentCode());
            if (!sameDDKeys) {
                if (routes.existsDD(cmd.companyId(), cmd.originDepartmentCode(), cmd.destDepartmentCode()))
                    throw new DomainException("route_already_exists");
            }
            route.updateDD(cmd.routeTypeId(), cmd.originDepartmentCode(), cmd.destDepartmentCode(), cmd.active());
            routes.save(route);
        } else { // PP
            boolean samePPKeys = route.getOriginProvince() != null && route.getDestProvince() != null
                    && route.getOriginProvince().getValue().equals(cmd.originProvinceCode())
                    && route.getDestProvince().getValue().equals(cmd.destProvinceCode());
            if (!samePPKeys) {
                if (routes.existsPP(cmd.companyId(), cmd.originProvinceCode(), cmd.destProvinceCode()))
                    throw new DomainException("route_already_exists");
            }
            route.updatePP(
                    cmd.routeTypeId(),
                    cmd.originDepartmentCode(), cmd.destDepartmentCode(),
                    cmd.originProvinceCode(), cmd.destProvinceCode(),
                    cmd.active()
            );
            routes.save(route);
        }
    }

    @Override
    @Transactional
    public void delete(DeleteProviderRouteCommand cmd, int actorAccountId) {
        // A) Autorización (ADMIN de la company)
        boolean ok = memberships.hasAnyRole(cmd.companyId(), actorAccountId, List.of("ADMIN"));
        if (!ok) throw new DomainException("forbidden_company_admin_required");

        var route = routes.findById(cmd.routeId())
                .orElseThrow(() -> new DomainException("route_not_found"));
        if (!route.getCompanyId().equals(cmd.companyId()))
            throw new DomainException("forbidden_different_company");

        routes.delete(route);
    }

    private void requireDepartmentExists(String code, String errorKey) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("department_code_required");
        if (!adminGeo.existsDepartmentCode(code)) throw new IllegalArgumentException(errorKey);
    }
    private void requireProvinceExists(String code, String errorKey) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("province_code_required");
        if (!adminGeo.existsProvinceCode(code)) throw new IllegalArgumentException(errorKey);
    }
}
