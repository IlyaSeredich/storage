package com.cloud.cloudstorage.service;

import com.cloud.cloudstorage.dto.BaseResourceResponseDto;
import com.cloud.cloudstorage.dto.FileResponseDto;
import com.cloud.cloudstorage.dto.FileUploadDto;
import com.cloud.cloudstorage.dto.StreamResourceDto;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface ResourceService {
    void createRootDirectory(Long userId);
    List<FileResponseDto> uploadFiles(String path, FileUploadDto fileUploadDto, User user);
    BaseResourceResponseDto createEmptyDirectory(String path, User user);
    List<BaseResourceResponseDto> getDirectoryContent(String directoryPath, User user);
    List<BaseResourceResponseDto> getSearchedContent(String query, User user);
    BaseResourceResponseDto moveResource(String from, String to, User user);
    StreamResourceDto downloadResource(String path, User user);
    void deleteResource(String path, User user);
    BaseResourceResponseDto getResourceInfo(String path, User user);
    boolean isResourceExisting(String path);
}
