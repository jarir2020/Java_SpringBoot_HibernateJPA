package com.jarirahmed.springmvc.repository;

import com.jarirahmed.springmvc.model.Post;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/** A deterministic repository used until database persistence is introduced. */
@Repository
public class InMemoryPostRepository implements PostRepository {
    private final Map<Integer, Post> posts = new LinkedHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(3);

    public InMemoryPostRepository() {
        posts.put(1, new Post(
                1,
                "Spring Core Basics",
                "The container creates beans and injects their required collaborators.",
                "teacher@example.com",
                "Backend",
                true));
        posts.put(2, new Post(
                2,
                "HTTP Contracts",
                "Methods, status codes, headers, and bodies form an API contract.",
                "teacher@example.com",
                "Web",
                true));
    }

    @Override
    public synchronized List<Post> findAll(String category, Boolean published) {
        return posts.values().stream()
                .filter(post -> category == null || category.isBlank()
                        || post.category().equalsIgnoreCase(category.trim()))
                .filter(post -> published == null || post.published() == published)
                .sorted(Comparator.comparingInt(Post::id))
                .toList();
    }

    @Override
    public synchronized Optional<Post> findById(int id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public synchronized boolean existsByTitleIgnoreCase(String title, int ignoredId) {
        return posts.values().stream()
                .anyMatch(post -> post.id() != ignoredId
                        && post.title().equalsIgnoreCase(title.trim()));
    }

    @Override
    public synchronized Post save(Post post) {
        int id = post.id() == 0 ? nextId.getAndIncrement() : post.id();
        Post stored = new Post(
                id,
                post.title().trim(),
                post.body().trim(),
                post.authorEmail().trim(),
                post.category().trim(),
                post.published());
        posts.put(id, stored);
        return stored;
    }

    @Override
    public synchronized boolean deleteById(int id) {
        return posts.remove(id) != null;
    }
}
