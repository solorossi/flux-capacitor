package org.solorossi.fluxcapacitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema( description = "Envelope containing response data or error messages" )
public record ApiResponse<T>(
        @Schema( description = "Was the request successful?",
                 example = "true" )
        boolean success,

        @Schema( description = "The request-specific response data" )
        T data,

        @Schema( description = "A summary message for the response" )
        String message,

        @Schema( description = "Collection of error messages (if any)" )
        List<String> errorMessages ) {

    // Helper method for successful responses
    public static <T> ApiResponse<T> success( T data, String message ) {

        return new ApiResponse<>( true, data, message, null );
    }

    // Helper method for error responses
    public static <T> ApiResponse<T> error( String message, List<String> errorMessages ) {

        return new ApiResponse<>( false, null, message, errorMessages );
    }
}
