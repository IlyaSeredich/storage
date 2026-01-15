package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioCreatingDirectoryException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE_TEMPLATE = "Creating directory %s failed";

    public MinioCreatingDirectoryException(String path) {
        super(createErrorMessage(path));
    }

    public static String createErrorMessage(String path) {
        return String.format(MESSAGE_TEMPLATE, path);
    }
}
