package com.cloud.cloudstorage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DirectoryPathValidator implements ConstraintValidator<ValidDirectoryPath, String> {
    @Override
    public boolean isValid(String path, ConstraintValidatorContext constraintValidatorContext) {
        return path.endsWith("/");
    }
}
