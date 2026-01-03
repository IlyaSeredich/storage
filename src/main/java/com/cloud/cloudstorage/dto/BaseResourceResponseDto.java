package com.cloud.cloudstorage.dto;

import com.cloud.cloudstorage.dto.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "Abstract class for DirectoryResponseDto and FileResponseDto")
@AllArgsConstructor
@Getter
public abstract class BaseResourceResponseDto {
    @Schema(description = "Parent path of the resource")
    private final String path;
    @Schema(description = "Name of the resource")
    private final String name;
    @Schema(description = "Type of the resource")
    private final ResourceType type;
}
