package com.jarirahmed.springmvc.repository;

import com.jarirahmed.springmvc.model.Post;

import java.util.List;
import java.util.Optional;

/** Persistence boundary. The controller and service do not know about maps. */
public interface PostRepository {
    List<Post> findAll(String category, Boolean published);

    Optional<Post> findById(int id);

    boolean existsByTitleIgnoreCase(String title, int ignoredId);

    Post save(Post post);

    boolean deleteById(int id);
}
