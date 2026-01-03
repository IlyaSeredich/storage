package com.cloud.cloudstorage.service;

public interface PathBuilderService {
    String createRootDirName(Long userId);
    String createFullDirectoryPath(Long userId, String path);
    String createFullFilePath(Long userId, String path, String fileName);
    String normalizePathFromRequest(String pathFromRequest);
}

