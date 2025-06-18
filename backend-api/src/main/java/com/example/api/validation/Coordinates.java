package com.example.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CoordinatesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Coordinates {
    String message() default "Invalid coordinates format (Valid: -90.0 to 90.0)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}