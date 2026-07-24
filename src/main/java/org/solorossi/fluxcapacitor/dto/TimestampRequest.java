package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Timestamp conversion request" )
public record TimestampRequest(
        @Schema( description = "Timestamp to convert",
                 example = "2026-04-01T16:14:12",
                 requiredMode = Schema.RequiredMode.REQUIRED )
        String timestamp,

        @Schema( description = "Time zone of timestamp",
                 example = "America/Chicago",
                 requiredMode = Schema.RequiredMode.REQUIRED )
        String sourceTimeZone,

        @Schema( description = "Desired time zone for conversion",
                 example = "America/New_York",
                 requiredMode = Schema.RequiredMode.REQUIRED )
        String destinationTimeZone ) {

}
