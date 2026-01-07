package com.blogg.blog_application.config;

import com.blogg.blog_application.model.Category;
import com.blogg.blog_application.model.User;
import com.blogg.blog_application.repository.CategoryRepository;
import com.blogg.blog_application.repository.UserRepository;

import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, 
                          CategoryRepository categoryRepository,
                          PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(new HashSet<>())
                        .enabled(true)
                        .build();
                admin.getRoles().add("ROLE_ADMIN");
                admin.getRoles().add("ROLE_USER"); // Admin can do everything
                userRepository.save(admin);
                System.out.println("✓ Admin user created: admin / admin123");
            }

            // Create a regular user for testing if not exists
            if (!userRepository.existsByUsername("user")) {
                User regularUser = User.builder()
                        .username("user")
                        .email("user@example.com")
                        .password(passwordEncoder.encode("user123"))
                        .roles(new HashSet<>())
                        .enabled(true)
                        .build();
                regularUser.getRoles().add("ROLE_USER");
                userRepository.save(regularUser);
                System.out.println("✓ Regular user created: user / user123");
            }

            // Create some default categories if not exists
            createCategoryIfNotExists(categoryRepository, "Technology", "Tech related posts");
            createCategoryIfNotExists(categoryRepository, "Lifestyle", "Lifestyle and personal posts");
            createCategoryIfNotExists(categoryRepository, "Travel", "Travel experiences and guides");
            createCategoryIfNotExists(categoryRepository, "Food", "Recipes and food reviews");
            
            System.out.println("✓ Data initialization completed!");
        };
    }

    private void createCategoryIfNotExists(CategoryRepository repo, String name, String desc) {
        if (repo.findByNameIgnoreCase(name).isEmpty()) {
            Category category = Category.builder()
                    .name(name)
                    .description(desc)
                    .build();
            repo.save(category);
            System.out.println("✓ Category created: " + name);
        }
    }
}