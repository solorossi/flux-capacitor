package org.solorossi.fluxcapacitor.service;

import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.springframework.validation.Errors;

public interface FluxCapacitorService {

    TimestampResponse convertTimestamp( TimestampRequest timestampRequest, Errors errors );

    OffsetResponse timeZoneDifference( OffsetRequest offsetRequest, Errors errors );
}
