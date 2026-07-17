package org.solorossi.fluxcapacitor.service.impl;

import org.junit.jupiter.api.Test;
import org.solorossi.fluxcapacitor.dto.OffsetRequest;
import org.solorossi.fluxcapacitor.dto.OffsetResponse;
import org.solorossi.fluxcapacitor.dto.TimestampRequest;
import org.solorossi.fluxcapacitor.dto.TimestampResponse;
import org.solorossi.fluxcapacitor.exception.BusinessErrors;
import org.solorossi.fluxcapacitor.service.ErrorMessageService;
import org.solorossi.fluxcapacitor.service.FluxCapacitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    @Test
    void testTimeZoneDifference() {

        BusinessErrors errors = new BusinessErrors();
        OffsetRequest request = new OffsetRequest( "America/Chicago", "America/Los_Angeles" );
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        // The timestamp value is for "now"; cursory check for validity. See the timestamp check test method.
        assertNotNull( response.timestamp() );
        assertTrue( response.timestamp().startsWith( "2" ) );
        assertTrue( response.timestamp().contains( "T" ) );
        assertTrue( response.timestamp().endsWith( "Z" ) );
        // The offsets depend on whether daylight saving time is on/off for "now".
        assertNotNull( response.sourceOffset() );
        assertNotNull( response.destinationOffset() );
        // The differences shouldn't matter w/r/t daylight saving.
        assertEquals( 7200, response.differenceInSeconds() );
        assertEquals( 2, response.differenceInHours() );

        // Use time zones that are always on standard time.
        errors = new BusinessErrors();
        request = new OffsetRequest( "America/Phoenix", "Pacific/Honolulu" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response.timestamp() );
        assertEquals( "-07:00", response.sourceOffset() );
        assertEquals( "-10:00", response.destinationOffset() );
        assertEquals( 10800, response.differenceInSeconds() );
        assertEquals( 3, response.differenceInHours() );
    }

    @Test
    void testDifferenceTimestamp() {

        BusinessErrors errors = new BusinessErrors();
        OffsetRequest request = new OffsetRequest( "America/Chicago", "America/Los_Angeles" );
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        String timestamp = response.timestamp();
        assertNotNull( timestamp );

        // Parse the timestamp for a more rigorous validation, since the timestamp is for "now".
        ZonedDateTime zonedDateTime = null;
        try {
            zonedDateTime = ZonedDateTime.parse( timestamp );
        }
        catch ( DateTimeParseException e ) {
            fail( e.getMessage() );
        }
        assertNotNull( zonedDateTime );
        assertEquals( "Z", zonedDateTime.getZone().toString() );
        assertTrue( zonedDateTime.getYear() >= 2026 );
    }

    // Test some shortcut time zone names, e.g. UTC, Z, UTC-3, +-N, etc.
    @Test
    void testDifferenceWithShortcutTimeZones() {

        BusinessErrors errors = new BusinessErrors();
        OffsetRequest request = new OffsetRequest( "UTC-5", "GMT" );
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response.timestamp() );
        assertEquals( "-05:00", response.sourceOffset() );
        assertEquals( "Z", response.destinationOffset() );
        assertEquals( 18000, response.differenceInSeconds() );
        assertEquals( 5, response.differenceInHours() );

        request = new OffsetRequest("UTC-4", "UTC+04:30" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response.timestamp() );
        assertEquals( "-04:00", response.sourceOffset() );
        assertEquals( "+04:30", response.destinationOffset() );
        assertEquals( 30600, response.differenceInSeconds() );
        assertEquals( 8.5, response.differenceInHours() );

        request = new OffsetRequest("-11", "+5" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response.timestamp() );
        assertEquals( "-11:00", response.sourceOffset() );
        assertEquals( "+05:00", response.destinationOffset() );
        assertEquals(57600, response.differenceInSeconds() );
        assertEquals( 16, response.differenceInHours() );

        request = new OffsetRequest("GMT-6", "Z" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        assertNotNull( response.timestamp() );
        assertEquals( "-06:00", response.sourceOffset() );
        assertEquals( "Z", response.destinationOffset() );
        assertEquals( 21600, response.differenceInSeconds() );
        assertEquals( 6, response.differenceInHours() );
    }

    // Test some old, deprecated time zone names
    @Test
    void testDifferenceWithOldTimeZones() {

        // Use time zones that don't use daylight saving time.
        BusinessErrors errors = new BusinessErrors();
        OffsetRequest request = new OffsetRequest( "HST", "IST" );
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertFalse( errors.hasErrors() );
        assertEquals( "-10:00", response.sourceOffset() );
        assertEquals( "+05:30", response.destinationOffset() );
        assertEquals(55800, response.differenceInSeconds() );
        assertEquals( 15.5, response.differenceInHours() );
    }

    // Test with blank/empty/null input strings.
    void testDifferenceWithEmptyInput() {

        BusinessErrors errors = new BusinessErrors();
        OffsetRequest request = new OffsetRequest( null, null );
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertTrue( errors.hasErrors() );
        // Make sure all errors are returned in one shot.
        assertTrue( errorsContains( errors, "A source time zone is required." ) );
        assertTrue( errorsContains( errors, "A destination time zone is required." ) );

        errors = new BusinessErrors();
        request = new OffsetRequest( "", "" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "A source time zone is required." ) );
        assertTrue( errorsContains( errors, "A destination time zone is required." ) );

        errors = new BusinessErrors();
        request = new OffsetRequest( "  ", "      " );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "A source time zone is required." ) );
        assertTrue( errorsContains( errors, "A destination time zone is required." ) );
    }

    @Test
    void testDifferenceWithBadTimeZones() {

        BusinessErrors errors = new BusinessErrors();
        OffsetRequest request = new OffsetRequest( "central", "pacific" );
        OffsetResponse response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "Unknown time-zone ID: central" ) );

        errors = new BusinessErrors();
        request = new OffsetRequest(  "America/Chicago", "pacific" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "Unknown time-zone ID: pacific" ) );

        errors = new BusinessErrors();
        request = new OffsetRequest( "America/Chicago", "EDT" );
        response = fluxCapacitorService.timeZoneDifference( request, errors );
        assertTrue( errors.hasErrors() );
        assertTrue( errorsContains( errors, "Unknown time-zone ID: EDT" ) );
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