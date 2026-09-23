package com.example.cowmarketplace.service.impl;

import com.example.cowmarketplace.exception.InvalidFileException;
import com.example.cowmarketplace.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Override
    public String store(MultipartFile file) {

        if (file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds 5MB limit");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "");

        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException("Only JPG, JPEG, PNG formats are allowed");
        }

        // Never trust the original filename for storage — generate our own
        String generatedFileName = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(generatedFileName).normalize();

            // Prevent path traversal: ensure the resolved path is still inside uploadDir
            if (!targetPath.startsWith(uploadPath)) {
                throw new InvalidFileException("Invalid file path");
            }

            Files.copy(file.getInputStream(), targetPath);

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new InvalidFileException("Failed to store file: " + e.getMessage());
        }

        // Returned as a relative URL the client can use directly, e.g. /images/uuid.jpg
        return "/images/" + generatedFileName;
    }

    @Override
    public void delete(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", imageUrl, e.getMessage());
            // Deliberately not rethrowing — a missing/already-deleted image file
            // shouldn't block the surrounding business operation (e.g. deleting a cow listing)
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            throw new InvalidFileException("File has no valid extension");
        }
        return filename.substring(lastDot + 1);
    }
}