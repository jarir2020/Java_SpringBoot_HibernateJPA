package com.jarirahmed.projects.blog.service;

import com.jarirahmed.projects.blog.domain.BlogComment;
import com.jarirahmed.projects.blog.domain.BlogPost;
import com.jarirahmed.projects.blog.domain.BlogUser;
import com.jarirahmed.projects.blog.dto.CommentResponse;
import com.jarirahmed.projects.blog.dto.CreateCommentRequest;
import com.jarirahmed.projects.blog.dto.CreatePostRequest;
import com.jarirahmed.projects.blog.dto.CreateUserRequest;
import com.jarirahmed.projects.blog.dto.PostDetailResponse;
import com.jarirahmed.projects.blog.dto.PostSummaryResponse;
import com.jarirahmed.projects.blog.dto.UserResponse;
import com.jarirahmed.projects.blog.error.BlogDuplicateException;
import com.jarirahmed.projects.blog.error.BlogNotFoundException;
import com.jarirahmed.projects.blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Application rules for users, posts, comments, and their relationships. */
@Service
public class BlogService {
    private final BlogRepository repository;

    public BlogService(BlogRepository repository) {
        this.repository = repository;
    }

    public List<UserResponse> listUsers() {
        return repository.findUsers().stream()
                .sorted(Comparator.comparingLong(BlogUser::id))
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse createUser(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (repository.findUserByEmail(email).isPresent()) {
            throw new BlogDuplicateException("A user already exists for email: " + email);
        }
        long id = repository.findUsers().stream()
                .mapToLong(BlogUser::id)
                .max()
                .orElse(0) + 1;
        BlogUser user = new BlogUser(id, request.displayName().trim(), email);
        return UserResponse.from(repository.saveUser(user));
    }

    public List<PostSummaryResponse> listPosts(String search) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return repository.findPosts().stream()
                .filter(post -> normalizedSearch.isBlank()
                        || post.title().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || post.body().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .sorted(Comparator.comparingLong(BlogPost::id).reversed())
                .map(this::toSummary)
                .toList();
    }

    public PostDetailResponse findPost(long id) {
        return toDetail(findPostEntity(id));
    }

    public PostDetailResponse createPost(CreatePostRequest request) {
        BlogUser author = findUserEntity(request.authorId());
        if (repository.existsPostTitle(request.title().trim(), 0)) {
            throw new BlogDuplicateException("A post already exists with this title.");
        }
        long id = repository.findPosts().stream()
                .mapToLong(BlogPost::id)
                .max()
                .orElse(0) + 1;
        BlogPost post = new BlogPost(
                id,
                request.title().trim(),
                request.body().trim(),
                author.id(),
                request.published());
        return toDetail(repository.savePost(post));
    }

    public PostDetailResponse updatePost(long id, CreatePostRequest request) {
        BlogPost post = findPostEntity(id);
        BlogUser author = findUserEntity(request.authorId());
        if (repository.existsPostTitle(request.title().trim(), id)) {
            throw new BlogDuplicateException("A post already exists with this title.");
        }
        post.update(request.title().trim(), request.body().trim(), author.id(), request.published());
        return toDetail(repository.savePost(post));
    }

    public void deletePost(long id) {
        if (!repository.deletePost(id)) {
            throw new BlogNotFoundException("post", id);
        }
    }

    public PostDetailResponse addComment(long postId, CreateCommentRequest request) {
        findPostEntity(postId);
        BlogUser author = findUserEntity(request.userId());
        long id = repository.nextCommentId();
        BlogComment comment = new BlogComment(id, postId, author.id(), request.body().trim());
        repository.saveComment(comment);
        return findPost(postId);
    }

    public void deleteComment(long id) {
        if (!repository.deleteComment(id)) {
            throw new BlogNotFoundException("comment", id);
        }
    }

    private PostSummaryResponse toSummary(BlogPost post) {
        String excerpt = post.body().length() <= 140
                ? post.body()
                : post.body().substring(0, 137) + "...";
        return new PostSummaryResponse(
                post.id(),
                post.title(),
                excerpt,
                UserResponse.from(findUserEntity(post.authorId())),
                post.published(),
                repository.findCommentsByPostId(post.id()).size());
    }

    private PostDetailResponse toDetail(BlogPost post) {
        List<CommentResponse> comments = repository.findCommentsByPostId(post.id()).stream()
                .map(comment -> CommentResponse.from(
                        comment,
                        UserResponse.from(findUserEntity(comment.userId()))))
                .toList();
        return new PostDetailResponse(
                post.id(),
                post.title(),
                post.body(),
                UserResponse.from(findUserEntity(post.authorId())),
                post.published(),
                comments);
    }

    private BlogPost findPostEntity(long id) {
        return repository.findPostById(id)
                .orElseThrow(() -> new BlogNotFoundException("post", id));
    }

    private BlogUser findUserEntity(long id) {
        return repository.findUserById(id)
                .orElseThrow(() -> new BlogNotFoundException("user", id));
    }
}
