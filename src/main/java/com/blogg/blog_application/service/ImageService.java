package com.blogg.blog_application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageService {

    private final Path uploadDir = Paths.get("uploads");

    public ImageService() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }

        String filename = UUID.randomUUID().toString() + ext;
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }
}
