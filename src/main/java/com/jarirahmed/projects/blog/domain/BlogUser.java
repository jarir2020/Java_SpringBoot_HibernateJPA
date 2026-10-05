package com.jarirahmed.projects.blog.domain;

/** An ordinary Java object representing a blog author or commenter. */
public final class BlogUser {
    private final long id;
    private final String displayName;
    private final String email;

    public BlogUser(long id, String displayName, String email) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
    }

    public long id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public String email() {
        return email;
    }
}
