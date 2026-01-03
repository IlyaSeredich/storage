package com.cloud.cloudstorage.service;

import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface MinioStorageService {
    void putRootDirectory(String rootDirName);
    void putEmptyDirectory(String fullPath);
    void putFile(String fullFilePath, MultipartFile multipartFile);
    boolean isResourceExisting(String path);
    List<Item> getDirectoryObjectsList(String directoryPath);
    List<Item> getWholeDirectoryContentList(String rootDir);
    void moveResource(String fullPathFrom, String fullPathTo);
    long getObjectSize(String fullPath);
    InputStream downloadResource(String fullPath);
    void deleteResource(String fullPath);
}
