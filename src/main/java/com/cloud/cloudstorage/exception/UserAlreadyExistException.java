package com.cloud.cloudstorage.exception;

public class UserAlreadyExistException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "User with username %s already exists";

    public UserAlreadyExistException(String username) {
        super(createErrorMessage(username));
    }

    public static String createErrorMessage(String username) {
        return String.format(MESSAGE_TEMPLATE, username);
    }

}