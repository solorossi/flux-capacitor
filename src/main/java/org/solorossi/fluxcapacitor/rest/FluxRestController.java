package org.solorossi.fluxcapacitor.rest;

import org.solorossi.fluxcapacitor.dto.ApiResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.exception.BusinessErrors;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( "/api" )
public class FluxRestController {

    private FluxCapacitorService fluxCapacitorService;

    @Autowired
    public FluxRestController( FluxCapacitorService fluxCapacitorService ) {

        this.fluxCapacitorService = fluxCapacitorService;
    }

    @GetMapping( "/help" )
    public String getHelp() {

        return "Help";
    }

    @PostMapping( "/convertTimestamp" )
    public ResponseEntity<ApiResponse<TimestampResponse>> convertTimestamp( @RequestBody TimestampRequest request ) {

        BusinessErrors errors = new BusinessErrors();
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        if ( errors.hasErrors() ) {
            return ResponseEntity.badRequest().body( ApiResponse.error( "Bad", List.of( "error1" ) ) );
        }

        return ResponseEntity.ok( ApiResponse.success( response, "Good" ) );
    }
}
