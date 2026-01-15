package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioResourceNotExistsException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE_TEMPLATE = "Resource %s not exists";

    public MinioResourceNotExistsException(String fullPath) {
        super(createErrorMessage(fullPath));
    }

    public static String createErrorMessage(String fullPath) {
        return String.format(MESSAGE_TEMPLATE, fullPath);
    }
}
