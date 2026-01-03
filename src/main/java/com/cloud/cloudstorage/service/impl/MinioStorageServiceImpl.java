package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.config.minio.MinioProperties;
import com.cloud.cloudstorage.exception.*;
import com.cloud.cloudstorage.service.MinioStorageService;
import io.minio.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioStorageServiceImpl implements MinioStorageService {
    private final PathAdapterServiceImpl pathAdapterService;
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioStorageServiceImpl(PathAdapterServiceImpl pathAdapterService, MinioClient minioClient, MinioProperties minioProperties) {
        this.pathAdapterService = pathAdapterService;
        this.minioClient = minioClient;
        this.bucketName = minioProperties.getBucket();
    }

    @Override
    public void putRootDirectory(String rootDirName) {
        try (InputStream inputStream = getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(rootDirName)
                    .stream(inputStream, 0, -1)
                    .contentType("application/x-directory")
                    .build());
        } catch (Exception ex) {
            throw new CreateRootMinioDirectoryException();
        }
    }

    @Override
    public void putEmptyDirectory(String fullPath) {
        try (InputStream inputStream = getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .stream(inputStream, 0, -1)
                            .contentType("application/x-directory")
                            .build()
            );
        } catch (Exception ex) {
            String pathForError = getPathForErrorMessage(fullPath);
            throw new MinioCreatingDirectoryException(pathForError);
        }
    }

    @Override
    public void putFile(String fullFilePath, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullFilePath)
                    .stream(inputStream, multipartFile.getSize(), -1)
                    .build());
        } catch (Exception ex) {
            throw new MinioUploadException();
        }
    }

    @Override
    public boolean isResourceExisting(String path) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public List<Item> getDirectoryObjectsList(String directoryPath) {
        Iterable<Result<Item>> directoryObjects = getDirectoryObjects(directoryPath);
        return convertMinioObjectsToList(directoryObjects, directoryPath);
    }

    @Override
    public List<Item> getWholeDirectoryContentList(String rootDir) {
        Iterable<Result<Item>> wholeContent = getWholeContent(rootDir);
        return convertMinioObjectsToList(wholeContent, rootDir);
    }

    @Override
    public void moveResource(String fullPathFrom, String fullPathTo) {
        if (fullPathFrom.endsWith("/")) {
            copyDirectory(fullPathFrom, fullPathTo);
            return;
        }
        copyFile(fullPathFrom, fullPathTo);
    }

    @Override
    public long getObjectSize(String fullPath) {
        StatObjectResponse statObjectResponse;
        try {
            statObjectResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .build()
            );
        } catch (Exception ex) {
            throw new MinioGetObjectSizeException();
        }
        return statObjectResponse.size();
    }

    @Override
    public InputStream downloadResource(String fullPath) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .build()
            );
        } catch (Exception ex) {
            throw new MinioDownloadResourceException();
        }
    }

    @Override
    public void deleteResource(String fullPath) {
        if(fullPath.endsWith("/")) {
            deleteDirectory(fullPath);
            return;
        }

        removeObject(fullPath);
    }

    private void deleteDirectory(String fullPath) {
        List<Item> contentList = getWholeDirectoryContentList(fullPath);
        contentList.forEach(item -> removeObject(item.objectName()));
        removeObject(fullPath);
    }

    private void copyFile(String fullPathFrom, String fullPathTo) {
        copyObject(fullPathFrom, fullPathTo);
        removeObject(fullPathFrom);
    }

    private void copyDirectory(String fullPathFrom, String fullPathTo) {
        putEmptyDirectory(fullPathTo);
        Iterable<Result<Item>> results = getWholeContent(fullPathFrom);
        copyDirectoryContent(results, fullPathFrom, fullPathTo);
        removeObject(fullPathFrom);
    }

    private void copyDirectoryContent(Iterable<Result<Item>> results, String fullPathFrom, String fullPathTo) {
        for (Result<Item> itemResult : results) {
            try {
                String objectName = itemResult.get().objectName();
                String relativePath = objectName.substring(fullPathFrom.length());

                if (relativePath.isBlank()) {
                    continue;
                }

                if (relativePath.contains("/")) {
                    createDirectories(fullPathTo, relativePath);
                }

                if (!relativePath.endsWith("/")) {
                    String targetObjectName = fullPathTo + relativePath;
                    copyFile(objectName, targetObjectName);
                }
                removeObject(objectName);
            } catch (Exception ex) {
                throw new MinioMovingException();
            }
        }
    }


    private void createDirectories(String fullPathTo, String relativePath) {
        int length;
        String[] directoryNames = relativePath.split("/");

        if (relativePath.endsWith("/")) length = directoryNames.length;
        else length = directoryNames.length - 1;

        StringBuilder directoryPath = new StringBuilder(fullPathTo);
        for (int i = 0; i < length; i++) {
            directoryPath.append(directoryNames[i]).append("/");
            putEmptyDirectory(directoryPath.toString());
        }
    }

    private void copyObject(String fullPathFrom, String fullPathTo) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPathTo)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(fullPathFrom)
                                    .build())
                            .build()
            );
        } catch (Exception ex) {
            throw new MinioMovingException();
        }
    }

    private void removeObject(String fullPathFrom) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPathFrom)
                            .build()
            );
        } catch (Exception ex) {
            throw new MinioMovingException();
        }
    }

    private Iterable<Result<Item>> getWholeContent(String path) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .recursive(true)
                        .build()
        );
    }

    private Iterable<Result<Item>> getDirectoryObjects(String directoryPath) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(directoryPath)
                        .delimiter("/")
                        .build()
        );
    }

    private List<Item> convertMinioObjectsToList(Iterable<Result<Item>> objects, String directoryPath) {
        List<Item> itemList = new ArrayList<>();

        objects.forEach(itemResult -> {
                    Item item;
                    try {
                        item = itemResult.get();
                    } catch (Exception ex) {
                        throw new MinioGettingDirectoryContentException();
                    }
                    if (!item.objectName().equals(directoryPath)) {
                        itemList.add(item);
                    }
                }
        );
        return itemList;
    }

    private InputStream getInputStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

    private String getPathForErrorMessage(String path) {
        return pathAdapterService.formatPathForErrorMessage(path);
    }
}
