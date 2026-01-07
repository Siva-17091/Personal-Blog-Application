package com.blogg.blog_application.service;

import com.blogg.blog_application.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> list();

    Optional<Category> get(Long id);

    Category save(Category category);

    void delete(Long id);
}
