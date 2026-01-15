package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioUploadingResourceAlreadyExistsException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE_TEMPLATE = "Resource %s already exists";

    public MinioUploadingResourceAlreadyExistsException(String fullPath) {
        super(createErrorMessage(fullPath));
    }

    public static String createErrorMessage(String path) {
        return String.format(MESSAGE_TEMPLATE, path);
    }
}
