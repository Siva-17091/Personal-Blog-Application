package com.blogg.blog_application.controller;

import com.blogg.blog_application.model.Post;
import com.blogg.blog_application.security.SecurityUtils;
import com.blogg.blog_application.service.CategoryService;
import com.blogg.blog_application.service.ImageService;
import com.blogg.blog_application.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final PostService postService;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    public AdminController(PostService postService,
                           ImageService imageService,
                           CategoryService categoryService,
                           SecurityUtils securityUtils) {
        this.postService = postService;
        this.imageService = imageService;
        this.categoryService = categoryService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalPosts", postService.count());
        return "admin/dashboard";
    }

    @GetMapping("/posts")
    public String listPosts(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> posts = postService.list(q, pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("q", q);
        return "admin/posts";
    }

    @GetMapping("/posts/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryService.list());
        return "admin/create_post";
    }

    @PostMapping("/posts")
    public String savePost(@Valid @ModelAttribute("post") Post post,
                           BindingResult result,
                           @RequestParam(value = "image", required = false) MultipartFile image,
                           @RequestParam(value = "categoryId", required = false) Long categoryId,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.list());
            return "admin/create_post";
        }

        try {
            // Handle image upload
            String filename = imageService.store(image);
            if (filename != null) {
                post.setImageFilename(filename);
            }

            // Set category if selected
            if (categoryId != null) {
                categoryService.get(categoryId).ifPresent(post::setCategory);
            }

            // Set current user as author
            post.setAuthor(securityUtils.getCurrentUser());
            
            postService.save(post);
            redirectAttributes.addFlashAttribute("success", "Post created successfully!");
            return "redirect:/admin/posts";
            
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            model.addAttribute("categories", categoryService.list());
            return "admin/create_post";
        }
    }

    @GetMapping("/posts/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Post> postOpt = postService.get(id);
        if (postOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Post not found!");
            return "redirect:/admin/posts";
        }
        model.addAttribute("post", postOpt.get());
        model.addAttribute("categories", categoryService.list());
        return "admin/edit_post";
    }

    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Long id,
                             @Valid @ModelAttribute("post") Post updatedPost,
                             BindingResult result,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @RequestParam(value = "categoryId", required = false) Long categoryId,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        Optional<Post> existingOpt = postService.get(id);
        if (existingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Post not found!");
            return "redirect:/admin/posts";
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.list());
            return "admin/edit_post";
        }

        Post existingPost = existingOpt.get();
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());

        try {
            // Handle image upload
            String filename = imageService.store(image);
            if (filename != null) {
                existingPost.setImageFilename(filename);
            }

            // Update category
            if (categoryId != null) {
                categoryService.get(categoryId).ifPresent(existingPost::setCategory);
            } else {
                existingPost.setCategory(null);
            }

            postService.save(existingPost);
            redirectAttributes.addFlashAttribute("success", "Post updated successfully!");
            return "redirect:/admin/posts";
            
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            model.addAttribute("categories", categoryService.list());
            return "admin/edit_post";
        }
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Post deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete post: " + e.getMessage());
        }
        return "redirect:/admin/posts";
    }
}