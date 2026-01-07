package com.blogg.blog_application.controller;

import com.blogg.blog_application.model.Post;
import com.blogg.blog_application.service.PostService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class BlogController {

    private final PostService postService;

    public BlogController(PostService postService){
        this.postService = postService;
    }

    @GetMapping("/")
    public String viewHomePage(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postsPage = postService.list(q, pageable);

        model.addAttribute("posts", postsPage);
        model.addAttribute("q", q);
        return "index";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable Long id, Model model){
        Optional<Post> postOpt = postService.get(id);
        if (postOpt.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("post", postOpt.get());
        return "post";
    }
}
