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
class FluxCapacitorServiceTest {

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
        assertEquals( "America/Los_Angeles", response.destinationTimeZone() );
    }

    @Test
    void testConversionWithUncommonTimeZones() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request =
                new TimestampRequest( "2027-02-28T18:00:00", "Pacific/Honolulu", "America/Puerto_Rico" );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2027-03-01T00:00:00", response.timestamp() );
    }

    // Test some shortcut time zone names, e.g. UTC, Z, UTC-3, +-N, etc.
    @Test
    void testConversionWithShortcutTimeZones() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request = new TimestampRequest( "2027-02-28T18:00:00", "UTC-5", "GMT" );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2027-02-28T23:00:00", response.timestamp() );

        request = new TimestampRequest( "2027-02-28T18:00:00", "UTC-4", "UTC+04:30" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2027-03-01T02:30:00", response.timestamp() );

        request = new TimestampRequest( "2027-02-28T18:00:00", "-11", "+5" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );

        assertEquals( "2027-03-01T10:00:00", response.timestamp() );

        request = new TimestampRequest( "2027-02-28T18:00:00", "GMT-6", "Z" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2027-03-01T00:00:00", response.timestamp() );
    }

    // Test some old, deprecated time zone names, e.g. CST, EST, HST, IST etc.
    @Test
    void testConversionWithOldTimeZones() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request = new TimestampRequest( "2027-02-28T18:00:00", "CST", "EST" );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2027-02-28T19:00:00", response.timestamp() );
        assertEquals( "America/Panama", response.destinationTimeZone() );

        request = new TimestampRequest( "2027-02-28T18:00:00", "HST", "IST" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response );
        assertEquals( "2027-03-01T09:30:00", response.timestamp() );
        assertEquals( "Asia/Kolkata", response.destinationTimeZone() );
    }

    // Test with blank/empty/null input strings.
    @Test
    void testConversionWithEmptyInput() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request = new TimestampRequest( null, null, null );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        // Make sure all errors are returned in one shot.
        assertTrue( errorsContains( errors, "A timestamp is required." ) );
        assertTrue( errorsContains( errors, "A source time zone is required." ) );
        assertTrue( errorsContains( errors, "A destination time zone is required." ) );

        errors = new BusinessErrors();
        request = new TimestampRequest( "", "", "" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "A timestamp is required." ) );
        assertTrue( errorsContains( errors, "A source time zone is required." ) );
        assertTrue( errorsContains( errors, "A destination time zone is required." ) );

        errors = new BusinessErrors();
        request = new TimestampRequest( "    ", " ", "     " );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "A timestamp is required." ) );
        assertTrue( errorsContains( errors, "A source time zone is required." ) );
        assertTrue( errorsContains( errors, "A destination time zone is required." ) );
    }

    // Test with bad input strings.
    @Test
    void testConversionWithBadTimestampInput() {

        BusinessErrors errors = new BusinessErrors();
        TimestampRequest request =
                new TimestampRequest( "2026-09-22X18:00:00", "America/Chicago", "America/Los_Angeles" );
        TimestampResponse response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "'2026-09-22X18:00:00' could not be parsed" ) );

        errors = new BusinessErrors();
        request = new TimestampRequest( "2026-09-22T18", "America/Chicago", "America/Los_Angeles" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "'2026-09-22T18' could not be parsed" ) );

        errors = new BusinessErrors();
        request = new TimestampRequest( "234278634T75", "America/Chicago", "America/Los_Angeles" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "'234278634T75' could not be parsed" ) );

        errors = new BusinessErrors();
        request = new TimestampRequest( "timestamp", "America/Chicago", "America/Los_Angeles" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "'timestamp' could not be parsed" ) );
    }

    @Test
    void testConversionWithBadTimeZones() {

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

        errors = new BusinessErrors();
        request = new TimestampRequest( "2026-09-22T18:00:00", "America/Chicago", "EDT" );
        response = fluxCapacitorService.convertTimestamp( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "Unknown time-zone ID: EDT" ) );
    }

    // Test time zone difference
    // Test some shortcut time zone names, e.g. UTC, Z, UTC-3, +-N, etc.
    // Test with blank/empty/null input strings.
    // Test with bad input strings.

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