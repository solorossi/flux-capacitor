package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema( description = "Time zone list response" )
public record TimeZoneResponse(
        @Schema( description = "Timestamp (in UTC) when request was made",
                 example = "2026-04-01T17:14:12Z" )
        String timestamp,

        @Schema( description = "Collection of time zone descriptions" )
        List<String> timeZones ) {
}
