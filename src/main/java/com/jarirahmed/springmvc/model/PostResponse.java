package com.jarirahmed.springmvc.model;

/** Response DTO: the API contract is separate from the internal model. */
public record PostResponse(
        int id,
        String title,
        String body,
        String authorEmail,
        String category,
        boolean published) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.id(),
                post.title(),
                post.body(),
                post.authorEmail(),
                post.category(),
                post.published());
    }
}
