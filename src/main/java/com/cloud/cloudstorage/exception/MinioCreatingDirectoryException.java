package com.cloud.cloudstorage.exception;

public class MinioCreatingDirectoryException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Creating directory %s failed";

    public MinioCreatingDirectoryException(String path) {
        super(createErrorMessage(path));
    }

    public static String createErrorMessage(String path) {
        return String.format(MESSAGE_TEMPLATE, path);
    }
}
