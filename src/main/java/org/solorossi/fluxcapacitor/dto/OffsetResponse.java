package org.solorossi.fluxcapacitor.dto;

public record OffsetResponse( String timestamp,
                              String sourceOffset,
                              String destinationOffset,
                              long differenceInSeconds,
                              double differenceInHours ) {

}
