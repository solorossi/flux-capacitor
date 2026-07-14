package org.solorossi.fluxcapacitor.service.impl;

import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class FluxCapacitorServiceImpl implements FluxCapacitorService {

    @Override
    public TimestampResponse convertTimestamp( TimestampRequest timestampRequest, Errors errors ) {

        return new TimestampResponse( "timestamp" );
    }
}
