package com.attachakki.validation;

import com.attachakki.validation.validator.PriceFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PriceFormatValidator.class)
public @interface PriceFormat {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "Invalid amount, It cannot be null, negative and more than 2 decimal places";
}

