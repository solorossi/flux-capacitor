package org.solorossi.fluxcapacitor.service.impl;

import org.solorossi.fluxcapacitor.dto.ApiResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.stereotype.Service;

@Service
public class FluxCapacitorServiceImpl implements FluxCapacitorService {

    @Override
    public ApiResponse<TimestampResponse> convertTimestamp( TimestampRequest timestampRequest ) {

        return ApiResponse.success( new TimestampResponse( "" ), null );
    }
}
