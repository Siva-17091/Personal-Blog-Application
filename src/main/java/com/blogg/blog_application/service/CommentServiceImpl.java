package com.blogg.blog_application.service;

import com.blogg.blog_application.model.Comment;
import com.blogg.blog_application.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment save(Comment comment){
        return commentRepository.save(comment);
    }
}
