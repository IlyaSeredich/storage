package com.cloud.cloudstorage.exception;

public class MinioGetObjectSizeException extends RuntimeException {
    private static final String MESSAGE = "Getting object size failed";

    public MinioGetObjectSizeException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
