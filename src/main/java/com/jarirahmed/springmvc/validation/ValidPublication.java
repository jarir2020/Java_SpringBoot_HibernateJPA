package com.jarirahmed.springmvc.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Cross-field rule: a published post must contain a substantial body. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PublishablePostValidator.class)
public @interface ValidPublication {
    String message() default "Published posts must contain at least 40 characters.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
