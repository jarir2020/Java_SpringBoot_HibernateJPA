package com.jarirahmed.projects.blog.domain;

/** A comment connected to one post and one user by their identifiers. */
public final class BlogComment {
    private final long id;
    private final long postId;
    private final long userId;
    private final String body;

    public BlogComment(long id, long postId, long userId, String body) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.body = body;
    }

    public long id() {
        return id;
    }

    public long postId() {
        return postId;
    }

    public long userId() {
        return userId;
    }

    public String body() {
        return body;
    }
}
