package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Time zone offset difference response" )
public record OffsetResponse(
        @Schema( description = "Timestamp (in UTC) when request was made",
                 example = "2026-04-01T17:14:12Z" )
        String timestamp,

        @Schema( description = "Time zone 1 offset from UTC",
                 example = "-05:00" )
        String sourceOffset,

        @Schema( description = "Time zone 2 offset from UTC",
                 example = "+05:30" )
        String destinationOffset,

        @Schema( description = "Difference between offsets, in seconds",
                 example = "37800" )
        long differenceInSeconds,

        @Schema( description = "Difference between offsets, in hours",
                 example = "10.5" )
        double differenceInHours ) {

}
