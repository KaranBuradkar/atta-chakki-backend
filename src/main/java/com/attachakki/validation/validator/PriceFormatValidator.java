package com.attachakki.validation.validator;

import com.attachakki.validation.PriceFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class PriceFormatValidator implements ConstraintValidator<PriceFormat, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal price, ConstraintValidatorContext constraintValidatorContext) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0 && price.scale() <= 2;
    }
}
