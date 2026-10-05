package com.jarirahmed.projects.blog.domain;

/** Mutable only through the explicit update method used by the service. */
public final class BlogPost {
    private final long id;
    private String title;
    private String body;
    private long authorId;
    private boolean published;

    public BlogPost(long id, String title, String body, long authorId, boolean published) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.authorId = authorId;
        this.published = published;
    }

    public long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String body() {
        return body;
    }

    public long authorId() {
        return authorId;
    }

    public boolean published() {
        return published;
    }

    public void update(String title, String body, long authorId, boolean published) {
        this.title = title;
        this.body = body;
        this.authorId = authorId;
        this.published = published;
    }
}
