package com.app.redcarga.requests.application.internal.views;

public record LocationView(
        String departmentCode,
        String departmentName,
        String provinceCode,
        String provinceName,
        String districtText
) {}
