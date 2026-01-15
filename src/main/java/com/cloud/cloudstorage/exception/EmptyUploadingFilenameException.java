package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class EmptyUploadingFilenameException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Filename is empty";

    public EmptyUploadingFilenameException(
    ) {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
