package com.cloud.cloudstorage.mapper;

import com.cloud.cloudstorage.dto.DirectoryResponseDto;
import com.cloud.cloudstorage.dto.FileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "path", source = "parentPathForResponse")
    @Mapping(target = "name", source = "directoryNameForResponse")
    @Mapping(target = "type", constant = "DIRECTORY")
     DirectoryResponseDto createDirectoryResponseDto(
            String parentPathForResponse,
            String directoryNameForResponse);

    @Mapping(target = "path", source = "parentPathForResponse")
    @Mapping(target = "name", source = "filenameForResponse")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "type", constant = "FILE")
     FileResponseDto createFileResponseDto(
             String parentPathForResponse,
             String filenameForResponse,
             Long size);
}
