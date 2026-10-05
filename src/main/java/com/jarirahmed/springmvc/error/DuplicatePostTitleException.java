package com.jarirahmed.springmvc.error;

/** Domain conflict mapped to HTTP 409 by the global advice. */
public class DuplicatePostTitleException extends RuntimeException {
    public DuplicatePostTitleException(String title) {
        super("A post with title '%s' already exists.".formatted(title));
    }
}
