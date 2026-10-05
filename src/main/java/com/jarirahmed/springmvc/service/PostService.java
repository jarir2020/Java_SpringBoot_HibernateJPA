package com.jarirahmed.springmvc.service;

import com.jarirahmed.springmvc.error.DuplicatePostTitleException;
import com.jarirahmed.springmvc.error.PostNotFoundException;
import com.jarirahmed.springmvc.model.CreatePostRequest;
import com.jarirahmed.springmvc.model.Post;
import com.jarirahmed.springmvc.model.PostResponse;
import com.jarirahmed.springmvc.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Comparator;

/** Application layer: business rules stay outside the HTTP controller. */
@Service
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public PostQueryResult findAll(
            String category,
            Boolean published,
            String search,
            int page,
            int size,
            String sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater.");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }

        List<Post> filtered = repository.findAll(category, published).stream()
                .filter(post -> matchesSearch(post, search))
                .sorted(comparatorFor(sort))
                .toList();

        long totalElements = filtered.size();
        int totalPages = totalElements == 0 ? 0 : (int) ((totalElements + size - 1) / size);
        long requestedFrom = (long) page * size;
        int fromIndex = requestedFrom >= filtered.size()
                ? filtered.size()
                : (int) requestedFrom;
        int toIndex = Math.min(fromIndex + size, filtered.size());
        List<PostResponse> posts = filtered.subList(fromIndex, toIndex).stream()
                .map(PostResponse::from)
                .toList();
        return new PostQueryResult(posts, page, size, totalElements, totalPages);
    }

    public PostResponse findById(int id) {
        return PostResponse.from(findPost(id));
    }

    public PostResponse create(CreatePostRequest request) {
        ensureTitleIsAvailable(request.title(), 0);
        return PostResponse.from(repository.save(new Post(
                0,
                request.title(),
                request.body(),
                request.authorEmail(),
                request.category(),
                request.published())));
    }

    public PostResponse update(int id, CreatePostRequest request) {
        findPost(id);
        ensureTitleIsAvailable(request.title(), id);
        return PostResponse.from(repository.save(new Post(
                id,
                request.title(),
                request.body(),
                request.authorEmail(),
                request.category(),
                request.published())));
    }

    public PostResponse updatePublication(int id, boolean published) {
        Post current = findPost(id);
        return PostResponse.from(repository.save(new Post(
                current.id(),
                current.title(),
                current.body(),
                current.authorEmail(),
                current.category(),
                published)));
    }

    public void delete(int id) {
        if (!repository.deleteById(id)) {
            throw new PostNotFoundException(id);
        }
    }

    private Post findPost(int id) {
        return repository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    private void ensureTitleIsAvailable(String title, int ignoredId) {
        if (repository.existsByTitleIgnoreCase(title, ignoredId)) {
            throw new DuplicatePostTitleException(title);
        }
    }

    private static boolean matchesSearch(Post post, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String normalized = search.trim().toLowerCase();
        return post.title().toLowerCase().contains(normalized)
                || post.body().toLowerCase().contains(normalized);
    }

    private static Comparator<Post> comparatorFor(String sort) {
        String requested = sort == null || sort.isBlank() ? "id" : sort.trim();
        String[] parts = requested.split(",", 2);
        Comparator<Post> comparator = switch (parts[0].toLowerCase()) {
            case "title" -> Comparator.comparing(Post::title, String.CASE_INSENSITIVE_ORDER);
            case "category" -> Comparator.comparing(Post::category, String.CASE_INSENSITIVE_ORDER);
            case "id" -> Comparator.comparingInt(Post::id);
            default -> throw new IllegalArgumentException(
                    "Sort must be one of: id, title, category.");
        };
        if (parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim())) {
            return comparator.reversed();
        }
        if (parts.length == 2 && !"asc".equalsIgnoreCase(parts[1].trim())) {
            throw new IllegalArgumentException("Sort direction must be asc or desc.");
        }
        return comparator;
    }
}
