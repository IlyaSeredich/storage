package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioGettingDirectoryContentException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Getting directory content failed";

    public MinioGettingDirectoryContentException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
