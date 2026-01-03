package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.service.PathBuilderService;
import com.cloud.cloudstorage.service.PathFormatterService;
import org.springframework.stereotype.Service;

@Service
public class PathAdapterServiceImpl implements PathBuilderService, PathFormatterService {
    private static final String ROOT_DIR_NAME_TEMPLATE = "user-%d-files/";

    @Override
    public String createRootDirName(Long userId) {
        return String.format(ROOT_DIR_NAME_TEMPLATE, userId);
    }

    @Override
    public String createFullDirectoryPath(Long userId, String path) {
        String rootDirName = createRootDirName(userId);
        String normalizedPath = normalizePathFromRequest(path);
        return rootDirName + normalizedPath;
    }

    @Override
    public String createFullFilePath(Long userId, String path, String filename) {
        String parentPath = createFullDirectoryPath(userId, path);
        return parentPath + filename;
    }

    @Override
    public String normalizePathFromRequest(String pathFromRequest) {
        return pathFromRequest.startsWith("/") ? pathFromRequest.substring(1) : pathFromRequest;
    }

    @Override
    public String formatPathForErrorMessage(String path) {
        int index = path.indexOf("/");
        return path.substring(index + 1);
    }

    @Override
    public String formatParentPathForResponse(String fullPath) {
        String parentDirectoryPath = extractParentPath(fullPath);
        int parentResponsePathFirstIndex = fullPath.indexOf("/");
        return parentDirectoryPath.substring(parentResponsePathFirstIndex + 1);
    }

    @Override
    public String formatDirectoryNameForResponse(String fullPath) {
        String parentPath = extractParentPath(fullPath);
        return fullPath.substring(parentPath.length(), fullPath.length() - 1);
    }

    @Override
    public String formatFilenameForResponse(String fullPath) {
        String parentPath = extractParentPath(fullPath);
        return fullPath.substring(parentPath.length());
    }

    @Override
    public String extractParentPath(String fullPath) {
        int penultimateSlashIndex = getPenultimateSlashIndex(fullPath);
        return fullPath.substring(0, penultimateSlashIndex + 1);
    }

    @Override
    public String extractResourceName(String fullPath) {
        int penultimateSlashIndex = getPenultimateSlashIndex(fullPath);
        return fullPath.substring(penultimateSlashIndex +1);
    }

    @Override
    public String formatFullPathForResponse(String fullPath) {
        int firstIndex = getFirstSlashIndex(fullPath);
        return fullPath.substring(firstIndex + 1);
    }

    private int getFirstSlashIndex(String fullPath) {
        return fullPath.indexOf("/");
    }
    private int getPenultimateSlashIndex(String fullPath) {
        return fullPath.lastIndexOf("/", fullPath.length() - 2);
    }
}
