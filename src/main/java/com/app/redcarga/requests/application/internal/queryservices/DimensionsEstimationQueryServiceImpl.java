package com.app.redcarga.requests.application.internal.queryservices;

import com.app.redcarga.requests.application.internal.gateways.MeasurementsGateway;
import com.app.redcarga.requests.application.internal.gateways.Upload;
import com.app.redcarga.requests.application.internal.views.DimensionsCmView;
import com.app.redcarga.shared.domain.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DimensionsEstimationQueryServiceImpl implements DimensionsEstimationQueryService {

    private final MeasurementsGateway measurements;

    @Override
    public DimensionsCmView estimate(Upload top, Upload side, double markerSizeMm, String requestId) {
        var r = measurements.estimate(top, side, markerSizeMm, requestId);

        if (r == null || r.dimensions_cm() == null) {
            throw new DomainException("measurements_unavailable");
        }
        var d = r.dimensions_cm();
        return new DimensionsCmView(d.width_cm(), d.length_cm(), d.height_cm());
    }
}
