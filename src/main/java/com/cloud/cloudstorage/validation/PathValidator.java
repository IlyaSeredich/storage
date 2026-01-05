package com.cloud.cloudstorage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PathValidator implements ConstraintValidator<ValidPath, String> {

    @Override
    public boolean isValid(String path, ConstraintValidatorContext context) {
        if(path == null) return true;

        return !path.contains("\\") && !path.contains("[") && !path.contains("]");
    }
}
