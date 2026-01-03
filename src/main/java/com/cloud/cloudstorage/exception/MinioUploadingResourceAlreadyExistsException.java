package com.cloud.cloudstorage.exception;

public class MinioUploadingResourceAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Resource %s already exists";

    public MinioUploadingResourceAlreadyExistsException(String fullPath) {
        super(createErrorMessage(fullPath));
    }

    public static String createErrorMessage(String path) {
        return String.format(MESSAGE_TEMPLATE, path);
    }
}
