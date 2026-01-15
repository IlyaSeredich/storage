package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class CreateRootMinioDirectoryException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Failed creating root Minio directory";

    public CreateRootMinioDirectoryException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
