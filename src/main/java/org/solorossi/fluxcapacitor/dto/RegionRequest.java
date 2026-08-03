package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Time zone region request" )
public record RegionRequest(
        @Schema( description = "Include special region names that do not follow the region/city naming scheme, " +
                               "i.e. those names that do not contain a slash." )
        boolean includeAlternativeRegions,

        @Schema( description = "Only request the special region names that do not follow the region/city naming scheme." )
        boolean onlyAlternativeRegions ) {
}
