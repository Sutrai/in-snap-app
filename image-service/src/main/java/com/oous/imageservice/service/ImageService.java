package com.oous.imageservice.service;

import com.oous.imageservice.dto.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Image uploadImage(Object image, String title, String description);
    MultipartFile downloadFileFromUrl(String url);
    String generateHash();
    byte[] getImage(String imageHash);
    Image imageInfo(String imageHash);
    boolean deleteImage(String imageHash);
    long getCurrentAccountId();
}

