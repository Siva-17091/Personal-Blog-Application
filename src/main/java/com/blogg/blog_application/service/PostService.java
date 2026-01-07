package com.blogg.blog_application.service;

import com.blogg.blog_application.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostService {

    Page<Post> list(String q, Pageable pageable);

    Optional<Post> get(Long id);

    Post save(Post post);

    void delete(Long id);

    long count();
}