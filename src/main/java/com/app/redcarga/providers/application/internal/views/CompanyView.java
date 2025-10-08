package com.app.redcarga.providers.application.internal.views;

public record CompanyView(
        int companyId,
        String legalName,
        String tradeName,
        String ruc,
        String email,
        String phone,
        String address,
        String status,
        String docsStatus,       // PENDING / COMPLETED (company_compliance)
        int createdByAccountId,  // para autorización (no es necesario exponerlo luego)
        int membersCount         // miembros activos
) {}