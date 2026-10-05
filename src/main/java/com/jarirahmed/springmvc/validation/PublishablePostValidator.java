package com.jarirahmed.springmvc.validation;

import com.jarirahmed.springmvc.model.CreatePostRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** Implements the cross-field validation rule used by CreatePostRequest. */
public final class PublishablePostValidator
        implements ConstraintValidator<ValidPublication, CreatePostRequest> {
    @Override
    public boolean isValid(CreatePostRequest request, ConstraintValidatorContext context) {
        if (request == null || !request.published()) {
            return true;
        }

        String body = request.body();
        boolean valid = body != null && body.trim().length() >= 40;
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Published posts must contain at least 40 characters.")
                    .addPropertyNode("body")
                    .addConstraintViolation();
        }
        return valid;
    }
}
