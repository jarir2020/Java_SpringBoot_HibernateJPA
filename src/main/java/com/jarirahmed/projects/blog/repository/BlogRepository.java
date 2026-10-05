package com.jarirahmed.projects.blog.repository;

import com.jarirahmed.projects.blog.domain.BlogComment;
import com.jarirahmed.projects.blog.domain.BlogPost;
import com.jarirahmed.projects.blog.domain.BlogUser;

import java.util.List;
import java.util.Optional;

/** Storage boundary for the Project 3 service. */
public interface BlogRepository {
    List<BlogUser> findUsers();

    Optional<BlogUser> findUserById(long id);

    Optional<BlogUser> findUserByEmail(String email);

    BlogUser saveUser(BlogUser user);

    List<BlogPost> findPosts();

    Optional<BlogPost> findPostById(long id);

    boolean existsPostTitle(String title, long ignoredPostId);

    BlogPost savePost(BlogPost post);

    boolean deletePost(long id);

    List<BlogComment> findCommentsByPostId(long postId);

    BlogComment saveComment(BlogComment comment);

    long nextCommentId();

    boolean deleteComment(long id);
}
