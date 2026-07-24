package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Timestamp conversion response" )
public record TimestampResponse(
        @Schema( description = "Converted timestamp",
                 example = "2026-04-01T17:14:12" )
        String timestamp,

        @Schema( description = "Desired time zone",
                 example = "America/New_York" )
        String destinationTimeZone ) {

}
