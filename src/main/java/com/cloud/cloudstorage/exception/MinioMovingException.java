package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class MinioMovingException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Moving failed";

    public MinioMovingException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
