package com.app.redcarga.requests.domain.repositories;

import com.app.redcarga.requests.domain.model.valueobjects.UbigeoSnapshot;

public record RequestNameAndUbigeo(Integer requestId, String requestName, UbigeoSnapshot origin, UbigeoSnapshot destination) {}