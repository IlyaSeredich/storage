package com.cloud.cloudstorage.exception;

public class MinioTypesNotMatchException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Resource type from - %s and to - %s not match";

    public MinioTypesNotMatchException(String pathFrom, String pathTo) {
        super(createErrorMessage(pathFrom, pathTo));
    }

    public static String createErrorMessage(String pathFrom, String pathTo) {
        return String.format(MESSAGE_TEMPLATE, pathFrom, pathTo);
    }
}
