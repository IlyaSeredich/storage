package com.cloud.cloudstorage.exception;

public class MinioExistingParentDirectoryException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Parent directory %s not found";

    public MinioExistingParentDirectoryException(String path) {
        super(createErrorMessage(path));
    }

    public static String createErrorMessage(String path) {
        return String.format(MESSAGE_TEMPLATE, path);
    }
}
