package org.solorossi.fluxcapacitor.dto;

import java.util.List;

public record ApiResponse<T>( boolean success, T data, String message, List<String> errorMessages ) {

    // Helper method for successful responses
    public static <T> ApiResponse<T> success( T data, String message ) {

        return new ApiResponse<>( true, data, message, null );
    }

    // Helper method for error responses
    public static <T> ApiResponse<T> error( String message, List<String> errorMessages ) {

        return new ApiResponse<>( false, null, message, errorMessages );
    }
}
