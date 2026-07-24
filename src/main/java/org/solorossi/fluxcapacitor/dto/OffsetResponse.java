package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Time zone offset difference response" )
public record OffsetResponse(
        @Schema( description = "Timestamp (in UTC) when request was made",
                 example = "2026-04-01T17:14:12Z" )
        String timestamp,
        String sourceOffset,
        String destinationOffset,
        long differenceInSeconds,
        double differenceInHours ) {

}
