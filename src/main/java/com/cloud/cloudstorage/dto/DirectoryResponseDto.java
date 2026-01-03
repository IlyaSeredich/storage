package com.cloud.cloudstorage.dto;

import com.cloud.cloudstorage.dto.enums.ResourceType;

public class DirectoryResponseDto extends BaseResourceResponseDto {
    public DirectoryResponseDto(String path, String name, ResourceType type) {
        super(path, name, type);
    }
}
