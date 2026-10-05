package com.jarirahmed.projects.blog.error;

/** Signals that a requested blog resource does not exist. */
public final class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException(String resource, long id) {
        super("No " + resource + " exists with id " + id + ".");
    }
}
