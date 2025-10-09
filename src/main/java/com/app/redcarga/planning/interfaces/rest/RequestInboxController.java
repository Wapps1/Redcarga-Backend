package com.app.redcarga.planning.interfaces.rest;

import com.app.redcarga.planning.application.internal.queryservices.RequestInboxQueryService;
import com.app.redcarga.planning.application.internal.views.RequestInboxEntryView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/planning/companies/{companyId}/request-inbox")
@RequiredArgsConstructor
// ...existing code...
@SecurityRequirement(name = "iam")
@PreAuthorize("hasRole('PROVIDER')")
public class RequestInboxController {

    private final RequestInboxQueryService queryService;

    @GetMapping
    public List<RequestInboxEntryResponse> findByCompany(@PathVariable int companyId) {
        List<RequestInboxEntryView> rows = queryService.findByCompany(companyId);
        return rows.stream().map(RequestInboxEntryResponse::from).collect(Collectors.toList());
    }

    public record RequestInboxEntryResponse(
            int requestId,
            int companyId,
            Integer matchedRouteId,
            Integer routeTypeId,
            String status,
            String createdAt, // ISO string
            String requesterName,
            String originDepartmentName,
            String originProvinceName,
            String destDepartmentName,
            String destProvinceName,
            Integer totalQuantity
    ) {
        static RequestInboxEntryResponse from(RequestInboxEntryView v) {
            return new RequestInboxEntryResponse(
                    v.requestId(),
                    v.companyId(),
                    v.matchedRouteId(),
                    v.routeTypeId(),
                    v.status(),
                    v.createdAt() != null ? v.createdAt().toString() : null,
                    v.requesterName(),
                    v.originDepartmentName(),
                    v.originProvinceName(),
                    v.destDepartmentName(),
                    v.destProvinceName(),
                    v.totalQuantity()
            );
        }
    }
}