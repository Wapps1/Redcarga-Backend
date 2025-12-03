package com.app.redcarga.deals.domain.queries;

import com.app.redcarga.deals.domain.model.entities.Assignment;
import com.app.redcarga.requests.domain.model.valueobjects.UbigeoSnapshot;

public record AcceptedAssignmentInfo(
        Assignment assignment,
        Integer quoteId,
        Integer requestId,
        String requestName,
        UbigeoSnapshot origin,
        UbigeoSnapshot destination
) {}
