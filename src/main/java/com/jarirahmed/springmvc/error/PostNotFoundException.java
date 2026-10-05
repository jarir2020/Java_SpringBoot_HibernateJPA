package com.jarirahmed.springmvc.error;

/** Domain failure mapped to HTTP 404 by the global advice. */
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(int id) {
        super("Post %d was not found.".formatted(id));
    }
}
