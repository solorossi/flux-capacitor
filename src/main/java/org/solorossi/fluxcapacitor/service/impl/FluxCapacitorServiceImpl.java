package org.solorossi.fluxcapacitor.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class FluxCapacitorServiceImpl implements FluxCapacitorService {

    @Override
    public TimestampResponse convertTimestamp( TimestampRequest timestampRequest, Errors errors ) {

        if ( StringUtils.isBlank( timestampRequest.timestamp() ) ) {
            errors.reject( "timestamp.request.timestamp.required" );
        }

        if ( StringUtils.isBlank( timestampRequest.sourceTimeZone() ) ) {
            errors.reject( "flux.request.sourceTimeZone.required" );
        }

        if ( StringUtils.isBlank( timestampRequest.destinationTimeZone() ) ) {
            errors.reject( "flux.request.destinationTimeZone.required" );
        }

        if ( errors.hasErrors() ) {
            return null;
        }

        // Handle old, deprecated 2-4 character zone names.
        String sourceTimeZone =
                ZoneId.SHORT_IDS.getOrDefault( timestampRequest.sourceTimeZone(), timestampRequest.sourceTimeZone() );
        String destinationTimeZone = ZoneId.SHORT_IDS.getOrDefault( timestampRequest.destinationTimeZone(),
                                                                    timestampRequest.destinationTimeZone() );
        String targetTimeString;

        try {
            LocalDateTime localDateTime = LocalDateTime.parse( timestampRequest.timestamp() );
            ZoneId sourceZone = ZoneId.of( sourceTimeZone );
            ZonedDateTime sourceTime = ZonedDateTime.of( localDateTime, sourceZone );

            ZoneId targetZone = ZoneId.of( destinationTimeZone );
            ZonedDateTime targetTime = sourceTime.withZoneSameInstant( targetZone );
            targetTimeString = targetTime.format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
        }
        catch ( Exception e ) {
            errors.reject( "exception.message", new Object[] { e.getMessage() }, null );
            return null;
        }

        return new TimestampResponse( targetTimeString, destinationTimeZone );
    }

    @Override
    public OffsetResponse timeZoneDifference( OffsetRequest offsetRequest, Errors errors ) {

        if ( StringUtils.isBlank( offsetRequest.sourceTimeZone() ) ) {
            errors.reject( "flux.request.sourceTimeZone.required" );
        }

        if ( StringUtils.isBlank( offsetRequest.destinationTimeZone() ) ) {
            errors.reject( "flux.request.destinationTimeZone.required" );
        }

        if ( errors.hasErrors() ) {
            return null;
        }

        // Handle old, deprecated 2-4 character zone names.
        String sourceTimeZone =
                ZoneId.SHORT_IDS.getOrDefault( offsetRequest.sourceTimeZone(), offsetRequest.sourceTimeZone() );
        String destinationTimeZone = ZoneId.SHORT_IDS.getOrDefault( offsetRequest.destinationTimeZone(),
                                                                    offsetRequest.destinationTimeZone() );
        String timestamp;
        String sourceOffset;
        String destinationOffset;
        long secondsDifference;
        double hoursDifference;

        try {
            // Get the time zones.
            ZoneId sourceZone = ZoneId.of( sourceTimeZone );
            ZoneId targetZone = ZoneId.of( destinationTimeZone );

            // Use a specific reference instant (e.g., right now)
            // Ignore nanoseconds
            Instant now = Instant.now().truncatedTo( ChronoUnit.SECONDS );
            ZonedDateTime dateTime = now.atZone( ZoneOffset.UTC );
            timestamp = dateTime.toString();

            // Get the specific rules and offsets for that instant
            ZoneOffset sourceZoneOffset = sourceZone.getRules().getOffset( now );
            ZoneOffset targetZoneOffset = targetZone.getRules().getOffset( now );

            // Pretty-print the offsets
            sourceOffset = sourceZoneOffset.toString();
            destinationOffset = targetZoneOffset.toString();

            // Calculate the difference in seconds
            secondsDifference = Math.abs( sourceZoneOffset.getTotalSeconds() - targetZoneOffset.getTotalSeconds() );

            // Convert the raw seconds to a readable Duration
            Duration duration = Duration.ofSeconds( secondsDifference );

            // Get decimal hours, since Duration.toHours() rounds down.
            hoursDifference = (double) duration.toMinutes() / Duration.ofHours(1).toMinutes();

        }
        catch ( Exception e ) {
            errors.reject( "exception.message", new Object[] { e.getMessage() }, null );
            return null;
        }

        return new OffsetResponse( timestamp, sourceOffset, destinationOffset, secondsDifference, hoursDifference );
    }
}
