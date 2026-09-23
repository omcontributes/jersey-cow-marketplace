package com.example.cowmarketplace.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Stores the file and returns a publicly accessible URL/path.
     * Implementation detail (local disk, S3, Cloudinary) is hidden behind this interface —
     * business logic (CowServiceImpl) never knows which one is used.
     */
    String store(MultipartFile file);

    void delete(String imageUrl);
}