package com.attachakki.validation.validator;

import com.attachakki.validation.CustomUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomUsernameValidator implements ConstraintValidator<CustomUsername, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (username == null || username.isBlank()) return false;
        return true;
    }
}
