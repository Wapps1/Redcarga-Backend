package com.app.redcarga.fleet.interfaces.rest.responses;

import java.util.Date;

public record DriverView(
        Integer driverId,
        Integer companyId,
        String fullName,
        String docNumber,
        String phone,
        String licenseNumber,
        boolean active,
        Date createdAt,
        Date updatedAt
) {}


