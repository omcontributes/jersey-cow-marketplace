package com.example.cowmarketplace.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.cowmarketplace.exception.InvalidFileException;
import com.example.cowmarketplace.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String FOLDER = "jerseycow";

    private final Cloudinary cloudinary;

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

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", FOLDER,
                            "resource_type", "image",
                            "unique_filename", true,
                            "overwrite", false));

            // Full permanent URL, e.g. https://res.cloudinary.com/alvv5jsq/image/upload/v123/jerseycow/abc.jpg
            return result.get("secure_url").toString();

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new InvalidFileException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public void delete(String imageUrl) {
        // Old listings may still have local "/images/..." paths: nothing to delete on Cloudinary
        if (imageUrl == null || !imageUrl.contains("res.cloudinary.com")) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (Exception e) {
            log.warn("Failed to delete image {}: {}", imageUrl, e.getMessage());
            // Don't rethrow: a failed image delete shouldn't block deleting the cow listing
        }
    }

    // .../image/upload/v1234567890/jerseycow/abc123.jpg  ->  jerseycow/abc123
    private String extractPublicId(String url) {
        String afterUpload = url.substring(url.indexOf("/upload/") + "/upload/".length());
        afterUpload = afterUpload.replaceFirst("^v\\d+/", "");   // remove version part
        int lastDot = afterUpload.lastIndexOf('.');
        return lastDot == -1 ? afterUpload : afterUpload.substring(0, lastDot);
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            throw new InvalidFileException("File has no valid extension");
        }
        return filename.substring(lastDot + 1);
    }
}