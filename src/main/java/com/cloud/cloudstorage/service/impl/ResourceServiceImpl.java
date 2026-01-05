package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.dto.*;
import com.cloud.cloudstorage.exception.*;
import com.cloud.cloudstorage.mapper.ResourceMapper;
import com.cloud.cloudstorage.service.*;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final CurrentUserService currentUserService;
    private final PathBuilderService pathBuilderService;
    private final PathFormatterService pathFormatterService;
    private final MinioStorageService minioStorageService;
    private final ResourceMapper resourceMapper;

    @Override
    public void createRootDirectory(Long userId) {
        String rootDirName = getRootDirName(userId);
        putRootDirectory(rootDirName);
    }

    @Override
    public DirectoryResponseDto createEmptyDirectory(String directoryPathFromRequest, User user) {
        String fullPath = getFullResourcePath(directoryPathFromRequest, user);
        validateCreatingDirectoryConditions(fullPath);
        putEmptyDirectory(fullPath);
        return getDirectoryResponseDto(fullPath);
    }

    @Override
    public List<FileResponseDto> uploadFiles(String parentPathFromRequest, FileUploadDto fileUploadDto, User user) {
        List<MultipartFile> multipartFileList = fileUploadDto.getMultipartFile();
        String fullParentPath = getFullResourcePath(parentPathFromRequest, user);
        validateFileParentDirectoryExists(fullParentPath);
        return uploadValidatedFiles(fullParentPath, multipartFileList);
    }

    @Override
    public List<BaseResourceResponseDto> getDirectoryContent(String directoryPathFromRequest, User user) {
        String fullPath = getFullResourcePath(directoryPathFromRequest, user);
        validateResourceExists(fullPath);
        List<Item> minioDirectoryContentList = getDirectoryContentList(fullPath);
        return createResourceResponseDtoList(minioDirectoryContentList);
    }

    @Override
    public List<BaseResourceResponseDto> getSearchedContent(String query, User user) {
        String rootDirName = getRootDirName(user);
        List<Item> wholeContentList = getWholeDirectoryContentList(rootDirName);
        List<Item> filteredBySearchQueryList = filterBySearchQuery(wholeContentList, query);
        return createResourceResponseDtoList(filteredBySearchQueryList);
    }

    @Override
    public BaseResourceResponseDto moveResource(String pathFrom, String pathTo, User user) {
        String fullPathFrom = getFullResourcePath(pathFrom, user);
        String fullPathTo = getFullResourcePath(pathTo, user);
        validateMovingConditions(fullPathFrom, fullPathTo);
        minioStorageService.moveResource(fullPathFrom, fullPathTo);
        return createResourceResponseDto(fullPathTo);
    }

    @Override
    public StreamResourceDto downloadResource(String path, User user) {
        String fullPath = getFullResourcePath(path, user);
        validateResourceExists(fullPath);
        if (isDirectoryPath(fullPath)) {
            return downloadDirectory(fullPath);
        } else {
            return downloadFile(fullPath);
        }
    }

    @Override
    public void deleteResource(String path, User user) {
        String fullPath = getFullResourcePath(path, user);
        validateResourceExists(fullPath);
        minioStorageService.deleteResource(fullPath);
    }

    @Override
    public BaseResourceResponseDto getResourceInfo(String path, User user) {
        String fullPath = getFullResourcePath(path, user);
        validateResourceExists(fullPath);
        return createResourceResponseDto(fullPath);
    }

    @Override
    public boolean isResourceExisting(String path) {
        return minioStorageService.isResourceExisting(path);
    }

    private StreamResourceDto downloadDirectory(String fullPath) {
        StreamingResponseBody body = getDirectoryStreamingResponseBody(fullPath);
        String directoryName = getDirectoryNameForResponse(fullPath);
        return new StreamResourceDto(body, directoryName + ".zip");
    }

    private StreamResourceDto downloadFile(String fullPath) {
        StreamingResponseBody body = getFileStreamingResponseBody(fullPath);
        String filename = getFilenameForResponse(fullPath);
        return new StreamResourceDto(body, filename);
    }

    private StreamingResponseBody getFileStreamingResponseBody(String fullPath) {
        return outputStream -> {
            try (InputStream inputStream = downloadResourceFromStorage(fullPath)) {
                StreamUtils.copy(inputStream, outputStream);
            }
        };
    }

    private StreamingResponseBody getDirectoryStreamingResponseBody(String fullPath) {
        return outputStream -> createZipArchive(outputStream, fullPath);
    }


    private void createZipArchive(OutputStream output, String fullPath) {
        String fullParentPath = getFullParentPath(fullPath);
        List<Item> wholeContentList = getWholeDirectoryContentList(fullPath);

        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            for (Item item : wholeContentList) {
                String objectName = item.objectName();
                try (InputStream input = downloadResourceFromStorage(objectName)) {
                    zip.putNextEntry(new ZipEntry(objectName.substring(fullParentPath.length())));
                    StreamUtils.copy(input, zip);
                    zip.closeEntry();
                }
            }
        } catch (Exception ex) {
            throw new MinioDownloadResourceException();
        }
    }

    private void validateMovingConditions(String pathFrom, String pathTo) {
        validateResourceExists(pathFrom);
        validateParentDirectoryExists(pathTo);
        validateResourceNotExists(pathTo);
        validateResourceTypeMatches(pathFrom, pathTo);
    }

    private void validateResourceTypeMatches(String pathFrom, String pathTo) {
        if (isDirectoryPath(pathFrom) && !isDirectoryPath(pathTo) || !isDirectoryPath(pathFrom) && isDirectoryPath(pathTo)) {
            String responsePathFrom = getPathForErrorMessage(pathFrom);
            String responsePathTo = getPathForErrorMessage(pathTo);
            throw new MinioTypesNotMatchException(responsePathFrom, responsePathTo);
        }
    }

    private BaseResourceResponseDto createResourceResponseDto(String fullPath) {
        if (isDirectoryPath(fullPath)) {
            return getDirectoryResponseDto(fullPath);
        }
        long objectSize = minioStorageService.getObjectSize(fullPath);
        return getFileResponseDto(fullPath, objectSize);
    }

    private List<Item> filterBySearchQuery(List<Item> wholeContentList, String query) {
        List<Item> filteredList = new ArrayList<>();
        wholeContentList.forEach(item -> {
            String fullPath = item.objectName();
            String upperItemName = pathFormatterService.extractResourceName(fullPath).toUpperCase();
            String upperQuery = query.toUpperCase();
            if (upperItemName.contains(upperQuery)) {
                filteredList.add(item);
            }
        });
        return filteredList;
    }

    private List<BaseResourceResponseDto> createResourceResponseDtoList(List<Item> directoryObjectsList) {
        List<BaseResourceResponseDto> dtoList = new ArrayList<>();
        directoryObjectsList.forEach(item -> {
            String itemName = item.objectName();
            if (isDirectoryPath(itemName)) {
                dtoList.add(getDirectoryResponseDto(itemName));
            } else {
                dtoList.add(getFileResponseDto(itemName, item.size()));
            }
        });
        return dtoList;
    }

    private String getRootDirName(Long userId) {
        return pathBuilderService.createRootDirName(userId);
    }

    private String getRootDirName(User user) {
        Long userId = getCurrentUserId(user);
        return pathBuilderService.createRootDirName(userId);
    }

    private void putRootDirectory(String rootDirName) {
        minioStorageService.putRootDirectory(rootDirName);
    }

    private List<Item> getDirectoryContentList(String fullPath) {
        return minioStorageService.getDirectoryObjectsList(fullPath);
    }

    private String getFullResourcePath(String pathFromRequest, User user) {
        Long userId = getCurrentUserId(user);
        return pathBuilderService.createFullDirectoryPath(userId, pathFromRequest);
    }

    private void validateCreatingDirectoryConditions(String fullPath) {
        validateParentDirectoryExists(fullPath);
        validateResourceNotExists(fullPath);
    }

    private void validateResourceExists(String fullPath) {
        if (!minioStorageService.isResourceExisting(fullPath)) {
            String pathForError = getPathForErrorMessage(fullPath);
            throw new MinioResourceNotExistsException(pathForError);
        }
    }

    private void putEmptyDirectory(String fullPath) {
        minioStorageService.putEmptyDirectory(fullPath);
    }

    private DirectoryResponseDto getDirectoryResponseDto(String fullPath) {
        String parentPathForResponse = getParentPathForResponse(fullPath);
        String directoryNameForResponse = getDirectoryNameForResponse(fullPath);
        return resourceMapper.createDirectoryResponseDto(parentPathForResponse, directoryNameForResponse);
    }

    private void validateFileParentDirectoryExists(String parentPath) {
        if (!isResourceExisting(parentPath)) {
            String pathForError = getPathForErrorMessage(parentPath);
            throw new MinioExistingParentDirectoryException(pathForError);
        }
    }

    private List<FileResponseDto> uploadValidatedFiles(String fullParentPath, List<MultipartFile> multipartFileList) {
        List<FileResponseDto> fileResponseDtoList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String filename = multipartFile.getOriginalFilename();
            validateFilename(filename);
            String fullFilePath = getFullFilePath(fullParentPath, filename);
            validateFileNotExists(fullFilePath);

            if (filename.contains("/")) {
                createDirectoriesFromFilename(fullParentPath, filename);
            }
            putFile(fullFilePath, multipartFile);
            fileResponseDtoList.add(
                    getFileResponseDto(fullFilePath, multipartFile.getSize())
            );
        }

        return fileResponseDtoList;
    }

    private Long getCurrentUserId(User user) {
        return currentUserService.getCurrentUserId(user);
    }

    private void validateParentDirectoryExists(String fullPath) {
        String parentPath = getFullParentPath(fullPath);

        if (!isResourceExisting(parentPath)) {
            String pathForError = getPathForErrorMessage(parentPath);
            throw new MinioExistingParentDirectoryException(pathForError);
        }
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new EmptyUploadingFilenameException();
        }
    }

    private void validateResourceNotExists(String fullPath) {
        if (isResourceExisting(fullPath)) {
            String pathForError = getPathForErrorMessage(fullPath);
            throw new MinioResourceAlreadyExistsException(pathForError);
        }
    }

    private void createDirectoriesFromFilename(String fullParentPath, String filename) {
        String[] split = filename.split("/");
        StringBuilder directoryPath = new StringBuilder(fullParentPath);
        for(int i = 0; i < split.length - 1; i++) {
            directoryPath.append(split[i]).append("/");
            putEmptyDirectory(directoryPath.toString());
        }
    }

    private String getPathForErrorMessage(String path) {
        return pathFormatterService.formatPathForErrorMessage(path);
    }

    private String getFullFilePath(String fullParentPath, String filename) {
        return fullParentPath + filename;
    }


    private void validateFileNotExists(String fullPath) {
        if (isResourceExisting(fullPath)) {
            String pathForError = getPathForErrorMessage(fullPath);
            throw new MinioUploadingResourceAlreadyExistsException(pathForError);
        }
    }

    private void putFile(String fullFilePath, MultipartFile multipartFile) {
        minioStorageService.putFile(fullFilePath, multipartFile);
    }

    private FileResponseDto getFileResponseDto(String fullPath, Long size) {
        String formatedParentPathForResponse = getParentPathForResponse(fullPath);
        String filenameForResponse = getFilenameForResponse(fullPath);
        return resourceMapper.createFileResponseDto(formatedParentPathForResponse, filenameForResponse, size);
    }

    private boolean isDirectoryPath(String path) {
        return path.endsWith("/");
    }

    private String getFilenameForResponse(String fullPath) {
        return pathFormatterService.formatFilenameForResponse(fullPath);
    }

    private String getDirectoryNameForResponse(String fullPath) {
        return pathFormatterService.formatDirectoryNameForResponse(fullPath);
    }

    private String getFullParentPath(String fullPath) {
        return pathFormatterService.extractParentPath(fullPath);
    }

    private String getParentPathForResponse(String fullPath) {
        return pathFormatterService.formatParentPathForResponse(fullPath);
    }

    private List<Item> getWholeDirectoryContentList(String fullPath) {
        return minioStorageService.getWholeDirectoryContentList(fullPath);
    }

    private InputStream downloadResourceFromStorage(String fullPath) {
        return minioStorageService.downloadResource(fullPath);
    }
}
