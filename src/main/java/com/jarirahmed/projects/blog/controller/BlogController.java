package com.jarirahmed.projects.blog.controller;

import com.jarirahmed.projects.blog.dto.CreateCommentRequest;
import com.jarirahmed.projects.blog.dto.CreatePostRequest;
import com.jarirahmed.projects.blog.dto.CreateUserRequest;
import com.jarirahmed.projects.blog.dto.PostDetailResponse;
import com.jarirahmed.projects.blog.dto.PostSummaryResponse;
import com.jarirahmed.projects.blog.dto.UserResponse;
import com.jarirahmed.projects.blog.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** REST boundary used by the Project 3 browser frontend. */
@RestController
@RequestMapping("/api/project3")
public class BlogController {
    private final BlogService service;

    public BlogController(BlogService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return service.listUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = service.createUser(request);
        return ResponseEntity.created(URI.create("/api/project3/users/" + created.id()))
                .body(created);
    }

    @GetMapping("/posts")
    public List<PostSummaryResponse> posts(
            @RequestParam(required = false) String search) {
        return service.listPosts(search);
    }

    @GetMapping("/posts/{id}")
    public PostDetailResponse post(@PathVariable long id) {
        return service.findPost(id);
    }

    @PostMapping("/posts")
    public ResponseEntity<PostDetailResponse> createPost(
            @Valid @RequestBody CreatePostRequest request) {
        PostDetailResponse created = service.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/project3/posts/" + created.id()))
                .body(created);
    }

    @PutMapping("/posts/{id}")
    public PostDetailResponse updatePost(
            @PathVariable long id,
            @Valid @RequestBody CreatePostRequest request) {
        return service.updatePost(id, request);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id) {
        service.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<PostDetailResponse> addComment(
            @PathVariable long id,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addComment(id, request));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable long id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
