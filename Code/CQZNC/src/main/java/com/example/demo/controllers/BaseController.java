package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.services.LookupService;
import com.example.demo.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

public abstract class BaseController {

    protected final LookupService lookupService;
    private static final Set<String> ALLOWED_IMAGE_EXT = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    public BaseController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @ModelAttribute("careers")
    public Object careers() {
        return lookupService.careers();
    }

    @ModelAttribute("jobTypes")
    public Object jobTypes() {
        return lookupService.jobTypes();
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        return SessionUtil.currentUser(session);
    }

    protected String storeAvatar(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String ext = ".jpg";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            String maybeExt = original.substring(dot);
            if (ALLOWED_IMAGE_EXT.contains(maybeExt)) {
                ext = maybeExt;
            }
        }

        String safePrefix = (prefix == null || prefix.isBlank() ? "user" : prefix).replaceAll("[^a-zA-Z0-9._-]", "_");
        String fileName = safePrefix + "_" + UUID.randomUUID() + ext;
        Path dir = Paths.get("uploads", "avatars");
        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Không thể lưu ảnh đại diện", e);
        }
    }
}
