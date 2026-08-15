package com.attachakki.validation;

import com.attachakki.validation.validator.CustomUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;

@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CustomUsernameValidator.class)
public @interface CustomUsername {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Invalid username format";
}
