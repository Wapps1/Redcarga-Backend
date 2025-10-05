package com.app.redcarga.admingeo.interfaces.rest;

import com.app.redcarga.admingeo.application.internal.views.GeoCatalogView;
import com.app.redcarga.admingeo.application.internal.views.GeoCatalogViewMapper;
import com.app.redcarga.admingeo.domain.model.queries.ListGeoCatalogQuery;
import com.app.redcarga.admingeo.domain.services.GeoCatalogQueryService;
import com.app.redcarga.admingeo.interfaces.rest.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeoCatalogController {

    private final GeoCatalogQueryService queries;

    @GetMapping("/catalog")
    public ResponseEntity<GetGeoCatalogResponse> getCatalog() {
        // 1) dominio → 2) view (app) → 3) response (rest)
        var geo = queries.handle(new ListGeoCatalogQuery());
        GeoCatalogView view = GeoCatalogViewMapper.toView(geo);

        var deps = view.departments().stream()
                .map(d -> new DepartmentItemResponse(d.code(), d.name()))
                .collect(Collectors.toList());

        var provs = view.provinces().stream()
                .map(p -> new ProvinceItemResponse(p.code(), p.departmentCode(), p.name()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new GetGeoCatalogResponse(deps, provs));
    }
}
