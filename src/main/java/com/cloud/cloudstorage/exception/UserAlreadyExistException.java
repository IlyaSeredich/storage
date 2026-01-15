package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class UserAlreadyExistException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE_TEMPLATE = "User with username %s already exists";

    public UserAlreadyExistException(String username) {
        super(createErrorMessage(username));
    }

    public static String createErrorMessage(String username) {
        return String.format(MESSAGE_TEMPLATE, username);
    }

}