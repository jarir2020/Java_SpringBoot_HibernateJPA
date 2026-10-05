package com.jarirahmed.projects.blog.error;

/** Signals a conflict with an existing user email or post title. */
public final class BlogDuplicateException extends RuntimeException {
    public BlogDuplicateException(String message) {
        super(message);
    }
}
