package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Time zone offset difference request" )
public record OffsetRequest(
        @Schema( description = "Time zone 1",
                 example = "America/Chicago",
                 requiredMode = Schema.RequiredMode.REQUIRED )
        String sourceTimeZone,

        @Schema( description = "Time zone 2",
                 example = "America/New_York",
                 requiredMode = Schema.RequiredMode.REQUIRED )
        String destinationTimeZone ) {

}
