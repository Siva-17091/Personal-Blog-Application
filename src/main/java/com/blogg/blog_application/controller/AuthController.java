package com.blogg.blog_application.controller;

import com.blogg.blog_application.model.User;
import com.blogg.blog_application.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                          BindingResult result,
                          @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already taken!");
            return "auth/register";
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "auth/register";
        }

        // Validate password confirmation
        if (confirmPassword == null || !user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "auth/register";
        }

        // Password strength validation
        if (user.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long!");
            return "auth/register";
        }

        // Create new user with ROLE_USER (regular user - can only view)
        User newUser = User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(new HashSet<>())
                .enabled(true)
                .build();
        
        // Regular users get ROLE_USER (can only view posts)
        newUser.getRoles().add("ROLE_USER");
        
        userRepository.save(newUser);
        
        redirectAttributes.addFlashAttribute("success", 
            "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}