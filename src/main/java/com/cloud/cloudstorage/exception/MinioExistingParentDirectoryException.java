package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioExistingParentDirectoryException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE_TEMPLATE = "Parent directory %s not found";

    public MinioExistingParentDirectoryException(String path) {
        super(createErrorMessage(path));
    }

    public static String createErrorMessage(String path) {
        return String.format(MESSAGE_TEMPLATE, path);
    }
}
