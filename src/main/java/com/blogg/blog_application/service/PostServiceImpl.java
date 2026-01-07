package com.blogg.blog_application.service;

import com.blogg.blog_application.model.Post;
import com.blogg.blog_application.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return postRepository.findAll(pageable);
        }
        return postRepository.findByTitleContainingIgnoreCase(q.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Post> get(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return postRepository.count();
    }
}