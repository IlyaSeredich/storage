package com.cloud.cloudstorage.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(description = "DTO for API error responses")
public record ErrorResponseDto(
        @Schema(description = "Error message")
        String message,
        @Schema(description = "HTTP reason phrase")
        String error,
        @Schema(description = "HTTP status code")
        int status,
        @Schema(description = "Request path that caused the error")
        String path,
        @Schema(description = "Timestamp of the error", type = "string", format = "date-time")
        LocalDateTime dateTime
) {
    public ErrorResponseDto(String message, HttpStatus httpStatus, String path) {
        this(
                message,
                httpStatus.getReasonPhrase(),
                httpStatus.value(),
                path,
                LocalDateTime.now()
        );
    }
}
