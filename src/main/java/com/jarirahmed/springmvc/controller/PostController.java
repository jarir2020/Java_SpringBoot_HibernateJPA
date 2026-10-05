package com.jarirahmed.springmvc.controller;

import com.jarirahmed.springmvc.model.CreatePostRequest;
import com.jarirahmed.springmvc.model.PostListResponse;
import com.jarirahmed.springmvc.model.PostResponse;
import com.jarirahmed.springmvc.model.SessionVisitResponse;
import com.jarirahmed.springmvc.service.PostService;
import com.jarirahmed.springmvc.service.PostQueryResult;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Controller lesson: maps HTTP requests to the application service and makes
 * status codes, headers, request data, and response bodies explicit.
 */
@RestController
@RequestMapping(path = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    public PostListResponse list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean published,
            @RequestHeader(value = "X-Client-Name", defaultValue = "anonymous") String clientName,
            @CookieValue(value = "course-mode", defaultValue = "standard") String courseMode,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort) {
        PostQueryResult result = service.findAll(category, published, search, page, size, sort);
        return new PostListResponse(
                result.posts(),
                clientName,
                courseMode,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages());
    }

    @GetMapping("/session")
    public SessionVisitResponse sessionVisits(HttpSession session) {
        Integer previousVisits = (Integer) session.getAttribute("postLessonVisits");
        int visits = previousVisits == null ? 1 : previousVisits + 1;
        session.setAttribute("postLessonVisits", visits);
        return new SessionVisitResponse(visits);
    }

    @GetMapping("/{id}")
    public PostResponse getById(@PathVariable int id) {
        return service.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> create(
            @Valid @RequestBody CreatePostRequest request,
            @RequestHeader(value = "X-Client-Name", defaultValue = "anonymous") String clientName) {
        PostResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/posts/" + created.id()))
                .header("X-Client-Name", clientName)
                .body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PostResponse update(
            @PathVariable int id,
            @Valid @RequestBody CreatePostRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/publication")
    public PostResponse updatePublication(
            @PathVariable int id,
            @RequestParam boolean published) {
        return service.updatePublication(id, published);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
