package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema( description = "Time zone region list response" )
public record RegionResponse(
        @Schema( description = "Collection of region names" )
        List<String> regions ) {

}
