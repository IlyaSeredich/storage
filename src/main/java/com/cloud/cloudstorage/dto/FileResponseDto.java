package com.cloud.cloudstorage.dto;

import com.cloud.cloudstorage.dto.enums.ResourceType;
import lombok.Getter;

@Getter
public class FileResponseDto extends BaseResourceResponseDto {
    private final Long size;

    public FileResponseDto(String path, String name, Long size, ResourceType type) {
        super(path, name, type);
        this.size = size;
    }

}
