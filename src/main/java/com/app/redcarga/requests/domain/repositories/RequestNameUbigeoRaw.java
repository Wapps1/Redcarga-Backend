package com.app.redcarga.requests.domain.repositories;

public record RequestNameUbigeoRaw(
    Integer requestId,
    String requestName,
    String originDepartmentCode,
    String originDepartmentName,
    String originProvinceCode,
    String originProvinceName,
    String originDistrictText,
    String destDepartmentCode,
    String destDepartmentName,
    String destProvinceCode,
    String destProvinceName,
    String destDistrictText
) {}
