package com.cloud.cloudstorage.exception;

public class UnauthorizedException extends RuntimeException{
    private static final String MESSAGE = "Full authentication is required";

    public UnauthorizedException() {
        super(getErrorMessage());
    }

    public static String getErrorMessage() {
        return MESSAGE;
    }
}
