package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioGetObjectSizeException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Getting object size failed";

    public MinioGetObjectSizeException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
