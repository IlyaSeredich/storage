package com.cloud.cloudstorage.exception;

public class CreateRootMinioDirectoryException extends RuntimeException {
    private static final String MESSAGE = "Failed creating root Minio directory";

    public CreateRootMinioDirectoryException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
