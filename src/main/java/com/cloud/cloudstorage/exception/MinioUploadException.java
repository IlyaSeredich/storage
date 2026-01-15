package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioUploadException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Uploading failed";

    public MinioUploadException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
