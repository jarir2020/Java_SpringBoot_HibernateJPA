package com.jarirahmed.projects.blog.repository;

import com.jarirahmed.projects.blog.domain.BlogComment;
import com.jarirahmed.projects.blog.domain.BlogPost;
import com.jarirahmed.projects.blog.domain.BlogUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A disposable repository for browser practice.
 *
 * <p>The seed data makes the page useful immediately. Restarting the app
 * resets it, which keeps database setup out of this MVC project.</p>
 */
@Repository
public class InMemoryBlogRepository implements BlogRepository {
    private final Map<Long, BlogUser> users = new LinkedHashMap<>();
    private final Map<Long, BlogPost> posts = new LinkedHashMap<>();
    private final Map<Long, BlogComment> comments = new LinkedHashMap<>();
    private final AtomicLong nextUserId = new AtomicLong(1);
    private final AtomicLong nextPostId = new AtomicLong(1);
    private final AtomicLong nextCommentId = new AtomicLong(1);

    public InMemoryBlogRepository() {
        saveUser(new BlogUser(nextUserId.getAndIncrement(), "Mira Rahman", "mira@example.com"));
        saveUser(new BlogUser(nextUserId.getAndIncrement(), "Hasan Karim", "hasan@example.com"));

        savePost(new BlogPost(
                nextPostId.getAndIncrement(),
                "Why Spring MVC still matters",
                "Spring MVC makes the HTTP boundary visible: a request enters through a controller, travels through a service, and becomes a response.",
                1,
                true));
        savePost(new BlogPost(
                nextPostId.getAndIncrement(),
                "Designing APIs beginners can grow into",
                "A small API is a good place to learn resources, status codes, validation, and error responses before adding a database.",
                2,
                true));

        saveComment(new BlogComment(
                nextCommentId.getAndIncrement(), 1, 2,
                "The layered boundary makes each lesson easier to test."));
    }

    @Override
    public List<BlogUser> findUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<BlogUser> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<BlogUser> findUserByEmail(String email) {
        String normalized = email.toLowerCase(Locale.ROOT);
        return users.values().stream()
                .filter(user -> user.email().equalsIgnoreCase(normalized))
                .findFirst();
    }

    @Override
    public BlogUser saveUser(BlogUser user) {
        users.put(user.id(), user);
        return user;
    }

    @Override
    public List<BlogPost> findPosts() {
        return new ArrayList<>(posts.values());
    }

    @Override
    public Optional<BlogPost> findPostById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public boolean existsPostTitle(String title, long ignoredPostId) {
        return posts.values().stream()
                .anyMatch(post -> post.id() != ignoredPostId
                        && post.title().equalsIgnoreCase(title));
    }

    @Override
    public BlogPost savePost(BlogPost post) {
        posts.put(post.id(), post);
        return post;
    }

    @Override
    public boolean deletePost(long id) {
        BlogPost removed = posts.remove(id);
        if (removed != null) {
            comments.values().removeIf(comment -> comment.postId() == id);
            return true;
        }
        return false;
    }

    @Override
    public List<BlogComment> findCommentsByPostId(long postId) {
        return comments.values().stream()
                .filter(comment -> comment.postId() == postId)
                .sorted(Comparator.comparingLong(BlogComment::id))
                .toList();
    }

    @Override
    public BlogComment saveComment(BlogComment comment) {
        comments.put(comment.id(), comment);
        return comment;
    }

    @Override
    public long nextCommentId() {
        return nextCommentId.getAndIncrement();
    }

    @Override
    public boolean deleteComment(long id) {
        return comments.remove(id) != null;
    }
}
