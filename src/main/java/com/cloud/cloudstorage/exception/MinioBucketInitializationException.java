package com.cloud.cloudstorage.exception;

public class MinioBucketInitializationException extends RuntimeException {
    private static final String MESSAGE = "Failed initialization minio bucket";

    public MinioBucketInitializationException(
    ) {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
