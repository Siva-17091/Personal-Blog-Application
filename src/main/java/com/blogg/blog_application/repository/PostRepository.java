package com.blogg.blog_application.repository;

import com.blogg.blog_application.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
