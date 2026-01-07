package com.blogg.blog_application.service;

import com.blogg.blog_application.model.Category;
import com.blogg.blog_application.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> list(){
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> get(Long id){
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category){
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id){
        categoryRepository.deleteById(id);
    }
}
