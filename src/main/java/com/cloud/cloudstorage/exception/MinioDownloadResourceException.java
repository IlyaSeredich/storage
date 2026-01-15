package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioDownloadResourceException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Downloading failed";

    public MinioDownloadResourceException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
