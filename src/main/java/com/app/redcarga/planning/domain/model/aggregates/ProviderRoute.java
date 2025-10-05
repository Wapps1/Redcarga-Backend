package com.app.redcarga.planning.domain.model.aggregates;

import com.app.redcarga.planning.domain.model.valueobjects.DepartmentCodePlanning;
import com.app.redcarga.planning.domain.model.valueobjects.ProvinceCodePlanning;
import com.app.redcarga.planning.domain.model.valueobjects.RouteShape;
import com.app.redcarga.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "planning", name = "provider_routes",
        uniqueConstraints = {
                // Nota: los índices únicos parciales (DD/PP) están en la BD;
                // aquí solo documentamos la unicidad de intención
        })
@AttributeOverrides({
        @AttributeOverride(name = "id",         column = @Column(name = "route_id")),
        @AttributeOverride(name = "createdAt",  column = @Column(name = "created_at",  nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt",  column = @Column(name = "updated_at",  nullable = false))
})
public class ProviderRoute extends AuditableAbstractAggregateRoot<ProviderRoute> {

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "route_type_id", nullable = false)
    private Integer routeTypeId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "origin_department_code", nullable = false, length = 2))
    private DepartmentCodePlanning originDepartment;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "dest_department_code", nullable = false, length = 2))
    private DepartmentCodePlanning destDepartment;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "origin_province_code", length = 4))
    private ProvinceCodePlanning originProvince; // NULL en DD

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "dest_province_code", length = 4))
    private ProvinceCodePlanning destProvince;   // NULL en DD

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /** Factory para shape DD (sin provincias) */
    public static ProviderRoute createDD(Integer companyId,
                                         Integer routeTypeId,
                                         String originDepCode,
                                         String destDepCode,
                                         Boolean isActive) {
        ProviderRoute r = new ProviderRoute();
        r.companyId     = requirePositive(companyId, "company_id");
        r.routeTypeId   = requirePositive(routeTypeId, "route_type_id");
        r.originDepartment = DepartmentCodePlanning.of(originDepCode);
        r.destDepartment   = DepartmentCodePlanning.of(destDepCode);
        r.originProvince   = null;
        r.destProvince     = null;
        r.active = (isActive == null) || Boolean.TRUE.equals(isActive);
        r.assertShape(RouteShape.DD);
        r.assertNotSameIfForbidden();
        return r;
    }

    /** Factory para shape PP (con provincias y prefijos coherentes) */
    public static ProviderRoute createPP(Integer companyId,
                                         Integer routeTypeId,
                                         String originDepCode,
                                         String destDepCode,
                                         String originProvCode,
                                         String destProvCode,
                                         Boolean isActive) {
        ProviderRoute r = new ProviderRoute();
        r.companyId     = requirePositive(companyId, "company_id");
        r.routeTypeId   = requirePositive(routeTypeId, "route_type_id");
        r.originDepartment = DepartmentCodePlanning.of(originDepCode);
        r.destDepartment   = DepartmentCodePlanning.of(destDepCode);
        r.originProvince   = ProvinceCodePlanning.of(originProvCode);
        r.destProvince     = ProvinceCodePlanning.of(destProvCode);
        r.active = (isActive == null) || Boolean.TRUE.equals(isActive);
        r.assertShape(RouteShape.PP);
        r.assertProvincePrefixes();
        r.assertNotSameIfForbidden();
        return r;
    }

    /** Activar/Desactivar */
    public void activate()  { this.active = true; }
    public void deactivate(){ this.active = false; }

    // --- Invariantes internas ---

    private void assertShape(RouteShape shape) {
        boolean provincesNull   = (originProvince == null && destProvince == null);
        boolean provincesNoNull = (originProvince != null && destProvince != null);

        if (shape == RouteShape.DD && !provincesNull)
            throw new IllegalArgumentException("route_shape_mismatch_dd_requires_null_provinces");

        if (shape == RouteShape.PP && !provincesNoNull)
            throw new IllegalArgumentException("route_shape_mismatch_pp_requires_both_provinces");
    }

    private void assertProvincePrefixes() {
        if (originProvince == null || destProvince == null)
            return; // Solo aplica a PP; DD no entra aquí

        String od = originDepartment.getValue();
        String dd = destDepartment.getValue();
        String op = originProvince.getValue();
        String dp = destProvince.getValue();

        if (!op.startsWith(od))
            throw new IllegalArgumentException("province_prefix_mismatch_origin");
        if (!dp.startsWith(dd))
            throw new IllegalArgumentException("province_prefix_mismatch_dest");
    }

    /** Si no permites A->A, descomenta la excepción; si sí lo permites, deja vacío. */
    private void assertNotSameIfForbidden() {
        // Regla opcional:
        // boolean sameDD = originDepartment.equals(destDepartment);
        // boolean samePP = originProvince != null && originProvince.equals(destProvince);
        // if (sameDD && (originProvince == null && destProvince == null))
        //     throw new IllegalArgumentException("route_origin_equals_destination_dd");
        // if (samePP)
        //     throw new IllegalArgumentException("route_origin_equals_destination_pp");
    }

    private static Integer requirePositive(Integer n, String field) {
        if (n == null || n <= 0) throw new IllegalArgumentException(field + "_invalid");
        return n;
    }
}
