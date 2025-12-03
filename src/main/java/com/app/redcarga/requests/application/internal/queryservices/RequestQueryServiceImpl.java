package com.app.redcarga.requests.application.internal.queryservices;

import com.app.redcarga.requests.domain.model.aggregates.Request;
import com.app.redcarga.requests.domain.repositories.RequestRepository;
import com.app.redcarga.requests.domain.repositories.RequestNameUbigeoRaw;
import com.app.redcarga.requests.domain.repositories.RequestNameAndUbigeo;
import com.app.redcarga.requests.domain.model.valueobjects.UbigeoSnapshot;
import com.app.redcarga.requests.domain.model.valueobjects.DepartmentCode;
import com.app.redcarga.requests.domain.model.valueobjects.ProvinceCode;
import com.app.redcarga.requests.domain.model.valueobjects.DistrictText;
import com.app.redcarga.requests.domain.services.RequestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestQueryServiceImpl implements RequestQueryService {

    private final RequestRepository requests;

    @Override
    public Optional<Request> findById(Integer requestId) {
        return requests.findById(requestId);
    }

    @Override
    public Optional<Request> findDetailById(Integer requestId) {
        return requests.findByIdWithItemsAndImages(requestId);
    }

    @Override
    public boolean existsById(Integer requestId) {
        return requests.existsById(requestId);
    }

    @Override
    public List<Request> findAllByRequester(Integer requesterAccountId) {
        return requests.findAllByRequesterAccountId(requesterAccountId);
    }

    @Override
    public boolean isRequester(Integer requestId, Integer accountId) {
        if (requestId == null || accountId == null) return false;
        return requests.findById(requestId)
                .map(r -> accountId.equals(r.getRequesterAccountId()))
                .orElse(false);
    }

    @Override
    public Optional<RequestNameAndUbigeo> getRequestNameById(Integer requestId) {
        if (requestId == null) return Optional.empty();

        return requests.findRequestNameById(requestId)
                .map(raw -> {
                    // Build origin UbigeoSnapshot
                    DepartmentCode odc = DepartmentCode.of(raw.originDepartmentCode());
                    ProvinceCode opc = ProvinceCode.ofNullable(raw.originProvinceCode());
                    DistrictText odt = DistrictText.ofNullable(raw.originDistrictText());
                    UbigeoSnapshot origin = UbigeoSnapshot.of(odc, raw.originDepartmentName(), opc, raw.originProvinceName(), odt);

                    // Build destination UbigeoSnapshot
                    DepartmentCode ddc = DepartmentCode.of(raw.destDepartmentCode());
                    ProvinceCode dpc = ProvinceCode.ofNullable(raw.destProvinceCode());
                    DistrictText ddt = DistrictText.ofNullable(raw.destDistrictText());
                    UbigeoSnapshot destination = UbigeoSnapshot.of(ddc, raw.destDepartmentName(), dpc, raw.destProvinceName(), ddt);

                    return new RequestNameAndUbigeo(raw.requestId(), raw.requestName(), origin, destination);
                });
    }
    
}
