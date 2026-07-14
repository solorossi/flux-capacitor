package org.solorossi.fluxcapacitor.service;

import org.solorossi.fluxcapacitor.dto.ApiResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;

public interface FluxCapacitorService {

    ApiResponse<TimestampResponse> convertTimestamp( TimestampRequest timestampRequest );
}
