package org.solorossi.fluxcapacitor.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.RegionRequest;
import org.solorossi.fluxcapacitor.dto.RegionResponse;
import org.solorossi.fluxcapacitor.dto.TimeZoneRequest;
import org.solorossi.fluxcapacitor.dto.TimeZoneResponse;
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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class implements the flux capacitor service layer for time zone request calculations.
 */
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
            // Parse the time zone, which has no zone information.
            LocalDateTime localDateTime = LocalDateTime.parse( timestampRequest.timestamp() );

            // Get the source time zone and put the timestamp into that zone.
            ZoneId sourceZone = ZoneId.of( sourceTimeZone );
            ZonedDateTime sourceTime = ZonedDateTime.of( localDateTime, sourceZone );

            // Get the destination time zone and get the same instant in that zone.
            ZoneId targetZone = ZoneId.of( destinationTimeZone );
            ZonedDateTime targetTime = sourceTime.withZoneSameInstant( targetZone );

            // Format the destination time without zone information.
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
            hoursDifference = (double) duration.toMinutes() / Duration.ofHours( 1 ).toMinutes();
        }
        catch ( Exception e ) {
            errors.reject( "exception.message", new Object[] { e.getMessage() }, null );
            return null;
        }

        return new OffsetResponse( timestamp, sourceOffset, destinationOffset, secondsDifference, hoursDifference );
    }

    @Override
    public RegionResponse getTimeZoneRegions( RegionRequest regionRequest, Errors errors ) {

        List<String> regions;

        try {
            // Filter out slash zone names if only the alternative names are requested.
            if ( regionRequest.onlyAlternativeRegions() ) {
                regions = getAlternativeRegionNames();
            }
            else {
                // Get ZoneIds that have region and location(s) with a slash ("/") in between
                regions = ZoneId.getAvailableZoneIds().stream()
                        .filter( id -> id.contains( "/" ) )
                        .map( id -> id.split( "/", 2 )[0] )
                        .distinct()
                        .sorted()
                        .toList();

                // Optionally add the alternative regions.
                if ( regionRequest.includeAlternativeRegions() ) {
                    List<String> alternativeRegions = getAlternativeRegionNames();
                    regions = Stream.concat( regions.stream(), alternativeRegions.stream() )
                            .distinct()
                            .sorted()
                            .toList();
                }
            }
        }
        catch ( Exception e ) {
            errors.reject( "exception.message", new Object[] { e.getMessage() }, null );
            return null;
        }

        return new RegionResponse( regions );
    }

    private List<String> getAlternativeRegionNames() {

        return ZoneId.getAvailableZoneIds().stream()
                .filter( id -> !id.contains( "/" ) )
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public TimeZoneResponse getTimeZones( TimeZoneRequest timeZoneRequest, Errors errors ) {

        String timestamp;
        List<String> timeZones;

        try {
            // Use a specific reference instant (e.g., right now)
            // Ignore nanoseconds
            Instant now = Instant.now().truncatedTo( ChronoUnit.SECONDS );
            ZonedDateTime dateTime = now.atZone( ZoneOffset.UTC );
            timestamp = dateTime.toString();

            Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();

            // Filter the full set if a region is requested.
            if ( StringUtils.isNotBlank( timeZoneRequest.region() ) ) {
                availableZoneIds = availableZoneIds.stream()
                        .filter( id -> id.startsWith( timeZoneRequest.region() ) )
                        .collect( Collectors.toSet() );
            }

            // Filter if a particular offset is requested.
            if ( StringUtils.isNotBlank( timeZoneRequest.offset() ) ) {

                // Try a few easy fix-ups, otherwise, let the Java Time library do the error processing.
                String offsetId = timeZoneRequest.offset();
                char first = offsetId.charAt( 0 );
                if ( first == 'Z' ) {
                    offsetId = "+00:00";
                }
                else if ( first == ' ' ) {
                    offsetId = "+" + offsetId.substring( 1 );
                }
                else if ( first != '+' && first != '-' ) {
                    offsetId = "+" + offsetId;
                }
                ZoneOffset zoneOffset = ZoneOffset.of( offsetId );
                availableZoneIds = availableZoneIds.stream()
                        .filter( id -> now.atZone( ZoneId.of( id ) ).getOffset().equals( zoneOffset ) )
                        .collect( Collectors.toSet() );
            }

            // Define a multi-level comparator - first sort by total seconds of the offset, then the zone name.
            Comparator<ZoneId> zoneComparator = Comparator
                    .<ZoneId, Integer>comparing( id -> id.getRules().getOffset( now ).getTotalSeconds() )
                    .thenComparing( ZoneId::getId );

            // Map the filtered zone names to a presentable list.
            timeZones = availableZoneIds.stream()
                    .map( ZoneId::of )
                    .sorted( zoneComparator )
                    .map( id -> String.format( "(%s) %s", getOffset( now, id ), id.getId() ) )
                    .toList();
        }
        catch ( Exception e ) {
            errors.reject( "exception.message", new Object[] { e.getMessage() }, null );
            return null;
        }

        return new TimeZoneResponse( timestamp, timeZones );
    }

    // Java represents +00:00 offsets as "Z"; replace it with a value consistent with other offsets.
    private String getOffset( Instant instant, ZoneId id ) {

        return instant
                .atZone( id )
                .getOffset()
                .getId()
                .replace( "Z", "+00:00" );
    }
}
