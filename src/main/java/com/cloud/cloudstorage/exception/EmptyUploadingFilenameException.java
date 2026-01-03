package com.cloud.cloudstorage.exception;

public class EmptyUploadingFilenameException extends RuntimeException {
    private static final String MESSAGE = "Filename is empty";

    public EmptyUploadingFilenameException(
    ) {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
