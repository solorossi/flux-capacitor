package org.solorossi.fluxcapacitor.service.impl;

import org.junit.jupiter.api.Test;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.exception.BusinessErrors;
import org.solorossi.fluxcapacitor.service.ErrorMessageService;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FluxCapacitorServiceImplTest {

    @Autowired FluxCapacitorService fluxCapacitorService;
    @Autowired ErrorMessageService errorMessageService;

    @Test
    void testConvertTimestamp() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request =
                new TimestampRequest( "2026-09-22T18:00:00", "America/Chicago", "America/Los_Angeles" );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2026-09-22T16:00:00", response.timestamp() );
    }

    @Test
    void testBadTimeZones() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request =
                new TimestampRequest( "2026-09-22T18:00:00", "central", "pacific" );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "Unknown time-zone ID: central" ) );

        errors = new BusinessErrors();
        request = new TimestampRequest( "2026-09-22T18:00:00", "America/Chicago", "pacific" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "Unknown time-zone ID: pacific" ) );
    }

    // Helper methods that should probably go in a utilities class

    private boolean errorsContains( Errors errors, String fragment ) {

        List<String> messages = errorMessageService.getMessages( errors );
        return errorsContains( messages, fragment );
    }

    private boolean errorsContains( List<String> messages, String fragment ) {

        if ( CollectionUtils.isEmpty( messages ) ) {
            return false;
        }

        return messages.stream().anyMatch( s -> s.contains( fragment ) );
    }
}