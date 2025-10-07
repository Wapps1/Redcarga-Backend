package com.app.redcarga.planning.application.internal.views;

public record ProviderRouteView (
        int routeId,
        int companyId,
        String companyName,
        String routeType,
        String originDepartmentCode,
        String originProvinceCode,
        String destDepartmentCode,
        String destProvinceCode,
        boolean active
){}
