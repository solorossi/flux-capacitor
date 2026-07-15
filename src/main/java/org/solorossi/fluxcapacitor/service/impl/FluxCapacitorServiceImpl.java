package org.solorossi.fluxcapacitor.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FluxCapacitorServiceImpl implements FluxCapacitorService {

    @Override
    public TimestampResponse convertTimestamp( TimestampRequest timestampRequest, Errors errors ) {

        if ( StringUtils.isBlank( timestampRequest.timestamp() ) ) {
            errors.reject( "timestamp.request.timestamp.required" );
        }

        if ( errors.hasErrors() ) {
            return null;
        }

        String targetTimeString;

        try {
            LocalDateTime localDateTime = LocalDateTime.parse( timestampRequest.timestamp() );
            ZoneId sourceZone = ZoneId.of( timestampRequest.sourceTimeZone() );
            ZonedDateTime sourceTime = ZonedDateTime.of( localDateTime, sourceZone );

            ZoneId targetZone = ZoneId.of( timestampRequest.destinationTimeZone() );
            ZonedDateTime targetTime = sourceTime.withZoneSameInstant( targetZone );
            targetTimeString = targetTime.format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
        }
        catch ( Exception e ) {
            errors.reject( "exception.message", new Object[] { e.getMessage() }, null );
            return null;
        }

        return new TimestampResponse( targetTimeString );
    }
}
