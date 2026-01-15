package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class UnauthorizedException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE = "Full authentication is required";

    public UnauthorizedException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
