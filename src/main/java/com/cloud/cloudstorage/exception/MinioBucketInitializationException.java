package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioBucketInitializationException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Failed initialization minio bucket";

    public MinioBucketInitializationException(
    ) {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
