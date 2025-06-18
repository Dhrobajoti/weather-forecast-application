package com.example.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CoordinatesValidator implements ConstraintValidator<Coordinates, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Coordinate cannot be empty")
                   .addConstraintViolation();
            return false;
        }
        
        try {
            double coord = Double.parseDouble(value);
            return coord >= -90 && coord <= 90;
        } catch (NumberFormatException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Must be a valid number")
                   .addConstraintViolation();
            return false;
        }
    }
}