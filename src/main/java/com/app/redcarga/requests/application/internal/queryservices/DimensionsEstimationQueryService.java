package com.app.redcarga.requests.application.internal.queryservices;

import com.app.redcarga.requests.application.internal.gateways.Upload;
import com.app.redcarga.requests.application.internal.views.DimensionsCmView;

public interface DimensionsEstimationQueryService {
    DimensionsCmView estimate(Upload top, Upload side, double markerSizeMm, String requestId);
}
