package org.solorossi.fluxcapacitor.rest;

import org.solorossi.fluxcapacitor.dto.ApiResponse;
import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.exception.BusinessErrors;
import org.solorossi.fluxcapacitor.service.ErrorMessageService;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping( "/convertTimestamp" )
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

    @PostMapping( "/timeZoneDifference" )
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
}
