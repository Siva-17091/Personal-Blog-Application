package com.blogg.blog_application.repository;

import com.blogg.blog_application.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
