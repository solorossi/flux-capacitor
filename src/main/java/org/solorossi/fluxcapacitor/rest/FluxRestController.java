package org.solorossi.fluxcapacitor.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.solorossi.fluxcapacitor.dto.ApiResponse;
import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.RegionRequest;
import org.solorossi.fluxcapacitor.dto.RegionResponse;
import org.solorossi.fluxcapacitor.dto.TimeZoneRequest;
import org.solorossi.fluxcapacitor.dto.TimeZoneResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.exception.BusinessErrors;
import org.solorossi.fluxcapacitor.service.ErrorMessageService;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag( name = "Flux Capacitor", description = "Time zone utilities" )
@RestController
@RequestMapping( "/api" )
public class FluxRestController {

    FluxCapacitorService fluxCapacitorService;
    ErrorMessageService errorMessageService;

    @Autowired
    public FluxRestController( FluxCapacitorService fluxCapacitorService, ErrorMessageService errorMessageService ) {

        this.fluxCapacitorService = fluxCapacitorService;
        this.errorMessageService = errorMessageService;
    }

    @Operation( summary = "Convert a timestamp with no time zone information from one time zone to another" )
    @PostMapping( "/timestamps" )
    public ResponseEntity<ApiResponse<TimestampResponse>> convertTimestamp( @RequestBody TimestampRequest request ) {

        BusinessErrors errors = new BusinessErrors();
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        if ( errors.hasErrors() ) {
            String message = errorMessageService.getMessage( "timestamp.conversion.failed" );
            List<String> errorMessages = errorMessageService.getMessages( errors );
            return ResponseEntity.badRequest().body( ApiResponse.error( message, errorMessages ) );
        }

        String message = errorMessageService.getMessage( "timestamp.conversion.successful" );
        return ResponseEntity.ok( ApiResponse.success( response, message ) );
    }

    @Operation( summary = "Calculate the time difference of offsets for two time zones" )
    @PostMapping( "/offsets" )
    public ResponseEntity<ApiResponse<OffsetResponse>> timeZoneDifference( @RequestBody OffsetRequest request ) {

        BusinessErrors errors = new BusinessErrors();
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        if ( errors.hasErrors() ) {
            String message = errorMessageService.getMessage( "time.zone.difference.failed" );
            List<String> errorMessages = errorMessageService.getMessages( errors );
            return ResponseEntity.badRequest().body( ApiResponse.error( message, errorMessages ) );
        }

        String message = errorMessageService.getMessage( "time.zone.difference.successful" );
        return ResponseEntity.ok( ApiResponse.success( response, message ) );
    }

    @Operation( summary = "Retrieve all primary time zone regions, typically core geographic regions " +
                          "such as America or Asia. These have a slash separating the primary region " +
                          "from a city with time zone rules, although there are some legacy region names. " +
                          "Optionally, retrieve special region names that do not use the region/city scheme." )
    @GetMapping( "/regions" )
    public ResponseEntity<ApiResponse<RegionResponse>> getRegions(
            @Parameter( description = "Include special region names that do not use the region/city scheme.",
                        example = "false" )
            @RequestParam( name = "includeAlternativeRegions", defaultValue = "false" )
            boolean includeAlternativeRegions,

            @Parameter( description = "Only return the special region names.", example = "false" )
            @RequestParam( name = "onlyAlternativeRegions", defaultValue = "false" )
            boolean onlyAlternativeRegions ) {

        BusinessErrors errors = new BusinessErrors();
        RegionRequest request = new RegionRequest( includeAlternativeRegions, onlyAlternativeRegions );
        RegionResponse response = fluxCapacitorService.getTimeZoneRegions( request, errors );
        if ( errors.hasErrors() ) {
            String message = errorMessageService.getMessage( "region.list.failed" );
            List<String> errorMessages = errorMessageService.getMessages( errors );
            return ResponseEntity.badRequest().body( ApiResponse.error( message, errorMessages ) );
        }

        String message = errorMessageService.getMessage( "region.list.successful" );
        return ResponseEntity.ok( ApiResponse.success( response, message ) );
    }

    @Operation( summary = "Retrieve time zones" )
    @GetMapping( "/time-zones" )
    public ResponseEntity<ApiResponse<TimeZoneResponse>> getTimeZones(
            @Parameter( description = "Region name, can be any value returned from the /regions endpoint, " +
                                      "or a string matching the beginning of any region name.",
                        example = "America" )
            @RequestParam( name = "region", required = false ) String region,

            @Parameter( description =
                                "Offset value, formatted as: Z (for UTC), +h, +hh, +hh:mm, -hh:mm, +hhmm, -hhmm;" +
                                " plus sign is optional",
                        example = "-05:00" )
            @RequestParam( name = "offset", required = false ) String offset ) {

        BusinessErrors errors = new BusinessErrors();
        TimeZoneRequest request = new TimeZoneRequest( region, offset );
        TimeZoneResponse response = fluxCapacitorService.getTimeZones( request, errors );
        if ( errors.hasErrors() ) {
            String message = errorMessageService.getMessage( "time.zone.list.failed" );
            List<String> errorMessages = errorMessageService.getMessages( errors );
            return ResponseEntity.badRequest().body( ApiResponse.error( message, errorMessages ) );
        }

        String message = errorMessageService.getMessage( "time.zone.list.successful" );
        return ResponseEntity.ok( ApiResponse.success( response, message ) );
    }
}
