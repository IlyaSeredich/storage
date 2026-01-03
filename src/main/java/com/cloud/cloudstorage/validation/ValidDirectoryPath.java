package com.cloud.cloudstorage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DirectoryPathValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDirectoryPath {
    String message() default "Path should have last symbol '/'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
