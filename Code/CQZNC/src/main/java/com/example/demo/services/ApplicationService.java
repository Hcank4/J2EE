package com.example.demo.services;

import com.example.demo.entities.Job;
import com.example.demo.entities.JobApplication;
import com.example.demo.entities.User;
import com.example.demo.repositories.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ApplicationService {
    private static final int MAX_CV_PER_USER = 3;
    private static final Set<String> ALLOWED_CV_EXT = Set.of(".pdf", ".doc", ".docx");
    private final JobApplicationRepository jobApplicationRepository;

    public ApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Value("${app.upload.dir}")
    private String uploadDir;

    public void apply(Job job, User user, String selectedCvName) {
        if (jobApplicationRepository.findByJobAndUser(job, user).isPresent()) {
            throw new IllegalArgumentException("Ban da ung tuyen cong viec nay roi");
        }
        String fileName = requireOwnedCv(user, selectedCvName);

        JobApplication app = new JobApplication();
        app.setJob(job);
        app.setUser(user);
        app.setEmployer(job.getEmployer());
        app.setCvPath(fileName);
        app.setStatus(0);
        app.setAppliedDate(LocalDateTime.now());
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());
        jobApplicationRepository.save(app);
    }

    public void updateStatus(Long applicationId, Integer status, User employer) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay ho so"));
        if (!app.getEmployer().getId().equals(employer.getId())) {
            throw new IllegalArgumentException("Ban khong co quyen cap nhat ho so nay");
        }
        app.setStatus(status);
        app.setUpdatedAt(LocalDateTime.now());
        jobApplicationRepository.save(app);
    }

    public List<JobApplication> byUser(User user) {
        return jobApplicationRepository.findByUserOrderByIdDesc(user);
    }

    public List<JobApplication> byEmployer(User employer) {
        return jobApplicationRepository.findByEmployerOrderByIdDesc(employer);
    }

    public List<UserCvItem> listUserCvItems(User user) {
        String prefix = cvPrefix(user);
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.startsWith(prefix))
                    .sorted(Comparator.reverseOrder())
                    .map(name -> new UserCvItem(name, extractDisplayName(name), "/uploads/cv/" + name))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Khong the doc danh sach CV", e);
        }
    }

    public void uploadUserCv(User user, MultipartFile cvFile) {
        if (cvFile == null || cvFile.isEmpty()) {
            throw new IllegalArgumentException("Vui long chon file CV");
        }
        List<UserCvItem> items = listUserCvItems(user);
        if (items.size() >= MAX_CV_PER_USER) {
            throw new IllegalArgumentException("Ban chi duoc luu toi da 3 CV");
        }

        String original = cvFile.getOriginalFilename() == null ? "cv" : cvFile.getOriginalFilename();
        String ext = extensionOf(original);
        if (!ALLOWED_CV_EXT.contains(ext)) {
            throw new IllegalArgumentException("Dinh dang CV khong hop le. Chi ho tro PDF, DOC, DOCX");
        }

        String fileName = cvPrefix(user) + UUID.randomUUID() + "_" + sanitizeFileName(original);
        store(cvFile, fileName);
    }

    public void deleteUserCv(User user, String cvName) {
        String ownedCv = requireOwnedCv(user, cvName);
        if (jobApplicationRepository.existsByUserAndCvPath(user, ownedCv)) {
            throw new IllegalArgumentException("Khong the xoa CV da dung de ung tuyen");
        }
        Path file = Paths.get(uploadDir).resolve(ownedCv);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IllegalStateException("Khong the xoa file CV", e);
        }
    }

    public String resolveCvForEmployer(Long applicationId, User employer) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay ho so"));
        if (!app.getEmployer().getId().equals(employer.getId())) {
            throw new IllegalArgumentException("Ban khong co quyen xem CV nay");
        }
        return ensureCvExistsAndGetPublicPath(app.getCvPath());
    }

    public String resolveCvForUser(Long applicationId, User user) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay ho so"));
        if (!app.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ban khong co quyen xem CV nay");
        }
        return ensureCvExistsAndGetPublicPath(app.getCvPath());
    }

    private String ensureCvExistsAndGetPublicPath(String cvPath) {
        if (cvPath == null || cvPath.isBlank()) {
            throw new IllegalArgumentException("Khong the tim thay file CV");
        }
        Path file = Paths.get(uploadDir).resolve(cvPath);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Khong the tim thay file CV");
        }
        return "/uploads/cv/" + cvPath;
    }

    private String requireOwnedCv(User user, String cvName) {
        if (cvName == null || cvName.isBlank()) {
            throw new IllegalArgumentException("Vui long chon CV de nop");
        }
        if (cvName.contains("/") || cvName.contains("\\") || cvName.contains("..")) {
            throw new IllegalArgumentException("CV khong hop le");
        }
        if (!cvName.startsWith(cvPrefix(user))) {
            throw new IllegalArgumentException("CV khong thuoc tai khoan cua ban");
        }
        Path file = Paths.get(uploadDir).resolve(cvName);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Khong the tim thay file CV");
        }
        return cvName;
    }

    private void store(MultipartFile file, String filename) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Khong the luu file CV", e);
        }
    }

    private String cvPrefix(User user) {
        return "u" + user.getId() + "_";
    }

    private String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot).toLowerCase(Locale.ROOT);
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String extractDisplayName(String storedName) {
        int firstUnderscore = storedName.indexOf('_');
        int secondUnderscore = firstUnderscore < 0 ? -1 : storedName.indexOf('_', firstUnderscore + 1);
        if (secondUnderscore >= 0 && secondUnderscore + 1 < storedName.length()) {
            return storedName.substring(secondUnderscore + 1);
        }
        return storedName;
    }

    public static class UserCvItem {
        private final String storedName;
        private final String displayName;
        private final String publicUrl;

        public UserCvItem(String storedName, String displayName, String publicUrl) {
            this.storedName = storedName;
            this.displayName = displayName;
            this.publicUrl = publicUrl;
        }

        public String getStoredName() {
            return storedName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPublicUrl() {
            return publicUrl;
        }
    }
}
