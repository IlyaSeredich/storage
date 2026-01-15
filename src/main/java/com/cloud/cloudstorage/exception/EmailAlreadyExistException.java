package com.cloud.cloudstorage.exception;

import com.cloud.cloudstorage.exception.marker.ExpectedException;

public class EmailAlreadyExistException extends RuntimeException implements ExpectedException {
    private static final String MESSAGE_TEMPLATE = "User with email %s already exists";

    public EmailAlreadyExistException(String email) {
        super(createErrorMessage(email));
    }

    public static String createErrorMessage(String email) {
        return String.format(MESSAGE_TEMPLATE, email);
    }

}