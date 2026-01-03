package com.cloud.cloudstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO with information about registered user")
public record UserResponseDto (
        @Schema(description = "Registered username of the user")
        String username
) { }