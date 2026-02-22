package com.storeapp.activity.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that end date/time is after start date/time
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeRangeValidator.class)
@Documented
public @interface ValidDateTimeRange {
    String message() default "Data e ora di fine devono essere successive a quelle di inizio";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
