package com.cloud.cloudstorage.exception;

public class MinioResourceAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Resource %s already exists";

    public MinioResourceAlreadyExistsException(String fullPath) {
        super(createErrorMessage(fullPath));
    }

    public static String createErrorMessage(String fullPath) {
        return String.format(MESSAGE_TEMPLATE, fullPath);
    }
}
