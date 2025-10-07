package com.app.redcarga.fleet.interfaces.rest.responses;

public record DriverView(
        Integer driverId,
        Integer companyId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String licenseNumber,
        boolean active,
        Long createdAt,
        Long updatedAt
) {}


