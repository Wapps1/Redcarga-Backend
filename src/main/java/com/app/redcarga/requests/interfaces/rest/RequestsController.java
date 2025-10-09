package com.app.redcarga.requests.interfaces.rest;

import com.app.redcarga.requests.application.internal.outboundservices.acl.IdentityPersonService;
import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.model.entities.RequestItem;
import com.app.redcarga.requests.domain.model.entities.RequestItemImage;
import com.app.redcarga.requests.domain.model.valueobjects.*;
import com.app.redcarga.requests.domain.model.commands.CreateRequestCommand;
import com.app.redcarga.requests.domain.model.commands.CreateRequestItemCommand;
import com.app.redcarga.requests.domain.model.commands.CreateRequestItemImageCommand;
import com.app.redcarga.requests.domain.services.RequestCommandService;
import com.app.redcarga.requests.domain.services.RequestQueryService;
import com.app.redcarga.requests.interfaces.rest.requests.CreateRequestRequest;
import com.app.redcarga.requests.interfaces.rest.responses.RequestCreatedResponse;
import com.app.redcarga.requests.interfaces.rest.responses.RequestDetailResponse;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import com.app.redcarga.shared.infrastructure.security.TokenClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.app.redcarga.requests.interfaces.rest.responses.RequestListItemResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
@PreAuthorize("hasRole('CLIENT')")
@RequiredArgsConstructor
@SecurityRequirement(name = "iam")
public class RequestsController {

    private final RequestCommandService commandService;
    private final RequestQueryService queryService;
    private final TokenClaims claims;
    private final IdentityPersonService identity;

    @PostMapping("/create-request")
    public ResponseEntity<RequestCreatedResponse> create(@Valid @RequestBody CreateRequestRequest body) {
        body.origin().validate();
        body.destination().validate();

        int accountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        UbigeoSnapshot origin = mapUbigeo(body.origin());
        UbigeoSnapshot destination = mapUbigeo(body.destination());

        var items = body.items().stream().map(it ->
                new CreateRequestItemCommand(
                        it.itemName(), it.heightCm(), it.widthCm(), it.lengthCm(),
                        it.weightKg(), it.totalWeightKg(), it.quantity(), it.fragile(), it.notes(),
                        it.images()==null? List.of() :
                                it.images().stream()
                                        .sorted(Comparator.comparing(img -> Optional.ofNullable(img.imagePosition()).orElse(Integer.MAX_VALUE)))
                                        .map(img -> new CreateRequestItemImageCommand(img.imageUrl(), img.imagePosition()))
                                        .toList()
                )
        ).toList();

        String requesterNameSnapshot = null;

        var cmd = new CreateRequestCommand(
                accountId, requesterNameSnapshot,
                origin, destination, items, body.paymentOnDelivery(), body.request_name()
        );

        Integer id = commandService.create(cmd);
        return ResponseEntity.ok(new RequestCreatedResponse(id));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDetailResponse> getById(@PathVariable Integer requestId) {
        int accountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        Request req = queryService.findById(requestId)
                .orElseThrow(() -> new DomainException("request_not_found"));

        if (!Integer.valueOf(accountId).equals(req.getRequesterAccountId()))
            throw new AccessDeniedException("not_owner");

        // ACL a Identity para traer el DNI (no se guarda en BD)
        var person = identity.getByAccountId(req.getRequesterAccountId());
        String docNumber = person.map(IdentityPersonService.PersonSnapshot::docNumber).orElse(null);

        return ResponseEntity.ok(toDetailResponse(req, docNumber));
    }


    /* ================== mappers ================== */

    private static UbigeoSnapshot mapUbigeo(CreateRequestRequest.UbigeoSnapshotRequest u) {
        DepartmentCode dep = DepartmentCode.of(u.departmentCode());                // si DepartmentCode SÍ tiene of(...)
        ProvinceCode prov  = ProvinceCode.ofNullable(u.provinceCode());            // <- antes usabas of(...)
        DistrictText dist  = DistrictText.ofNullable(u.districtText());            // <- antes usabas of(...)
        return UbigeoSnapshot.of(dep, u.departmentName(), prov, u.provinceName(), dist);
    }

    private static RequestDetailResponse toDetailResponse(Request r, String docNumber) {
        return new RequestDetailResponse(
                r.getId(),
                r.getRequesterAccountId(),
                r.getRequesterNameSnapshot(),
                r.getRequestName(),
                docNumber,                                  // <-- DNI desde ACL
                r.currentStatus().name(),
                r.getCreatedAt().toInstant(),
                r.getUpdatedAt().toInstant(),
                r.getClosedAt()==null? null : r.getClosedAt().toInstant(),
                new RequestDetailResponse.UbigeoSnapshotResponse(
                        val(r.getOrigin().getDepartmentCode()),
                        r.getOrigin().getDepartmentName(),
                        val(r.getOrigin().getProvinceCode()),
                        r.getOrigin().getProvinceName(),
                        val(r.getOrigin().getDistrictText())
                ),
                new RequestDetailResponse.UbigeoSnapshotResponse(
                        val(r.getDestination().getDepartmentCode()),
                        r.getDestination().getDepartmentName(),
                        val(r.getDestination().getProvinceCode()),
                        r.getDestination().getProvinceName(),
                        val(r.getDestination().getDistrictText())
                ),
                r.getItemsCount(),
                r.getTotalWeightKg(),
                r.isPaymentOnDelivery(),
                r.getItems().stream().map(RequestsController::toItemResponse).toList()
        );
    }

    private static RequestDetailResponse.RequestItemResponse toItemResponse(RequestItem it) {
        return new RequestDetailResponse.RequestItemResponse(
                it.getId(),
                it.getItemName().getValue(),
                it.getHeightCm(), it.getWidthCm(), it.getLengthCm(),
                it.getWeightKg(), it.getTotalWeightKg(),
                it.getQuantity(), it.isFragile(), it.getNotes(), it.getPosition(),
                it.getImages().stream()
                        .map(RequestsController::toImageResponse)
                        .toList()
        );
    }

    private static RequestDetailResponse.RequestItemImageResponse toImageResponse(RequestItemImage im) {
        return new RequestDetailResponse.RequestItemImageResponse(
                im.getId(), im.getImageUrl(), im.getImagePosition()
        );
    }

    private static String val(Object vo) {
        if (vo == null) return null;
        // soporta DepartmentCode/ProvinceCode/DistrictText: todos tienen toString() o getValue()
        try {
            return (String) vo.getClass().getMethod("getValue").invoke(vo);
        } catch (Exception e) {
            return vo.toString();
        }
    }

    @Operation(summary = "Get the list of requests (CLIENT)")
    @GetMapping
    public ResponseEntity<List<RequestListItemResponse>> listMine() {
        int accountId = claims.accountIdClaim()
                .orElseThrow(() -> new AccessDeniedException("account_id_missing"));

        List<RequestListItemResponse> items = queryService.findAllByRequester(accountId).stream()
                .sorted(Comparator.comparing(Request::getCreatedAt).reversed())
                .map(this::toListItemResponse)
                .toList();

        return ResponseEntity.ok(items);
    }

    private RequestListItemResponse toListItemResponse(Request req) {
        return new RequestListItemResponse(
                req.getId(),
                req.getRequestName(),
                req.currentStatus().name(),
                req.getCreatedAt() != null ? req.getCreatedAt().toInstant() : null,
                req.getUpdatedAt() != null ? req.getUpdatedAt().toInstant() : null,
                req.getClosedAt()  != null ? req.getClosedAt().toInstant()  : null,
                toLocationResponse(req.getOrigin()),
                toLocationResponse(req.getDestination()),
                req.getItemsCount(),
                req.getTotalWeightKg(),
                req.isPaymentOnDelivery()
        );
    }

    private static RequestListItemResponse.LocationResponse toLocationResponse(UbigeoSnapshot u) {
        return new RequestListItemResponse.LocationResponse(
                val(u.getDepartmentCode()), u.getDepartmentName(),
                val(u.getProvinceCode()),   u.getProvinceName(),
                val(u.getDistrictText())
        );
    }

}
