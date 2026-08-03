package org.solorossi.fluxcapacitor.service;

import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.RegionRequest;
import org.solorossi.fluxcapacitor.dto.RegionResponse;
import org.solorossi.fluxcapacitor.dto.TimeZoneRequest;
import org.solorossi.fluxcapacitor.dto.TimeZoneResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.springframework.validation.Errors;

/**
 * This interface defines the flux capacitor service for time zone requests.
 */
public interface FluxCapacitorService {

    TimestampResponse convertTimestamp( TimestampRequest timestampRequest, Errors errors );

    OffsetResponse timeZoneDifference( OffsetRequest offsetRequest, Errors errors );

    RegionResponse getTimeZoneRegions( RegionRequest regionRequest, Errors errors );

    TimeZoneResponse getTimeZones( TimeZoneRequest timeZoneRequest, Errors errors );
}
