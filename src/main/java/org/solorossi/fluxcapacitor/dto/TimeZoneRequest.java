package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Time zone list request" )
public record TimeZoneRequest(
        @Schema( description = "Filter time zone list to only include a particular region",
                 example = "America",
                 requiredMode = Schema.RequiredMode.NOT_REQUIRED )
        String region,

        @Schema( description = "Filter time zone list to only include a particular offset",
                 example = "-05:00",
                 requiredMode = Schema.RequiredMode.NOT_REQUIRED )
        String offset ) {
}
