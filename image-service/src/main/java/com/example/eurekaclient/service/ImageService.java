package com.example.eurekaclient.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageService {

    String uploadImage(MultipartFile file) throws IOException;
    MultipartFile downloadFileFromUrl(String url) throws Exception;
    MultipartFile validationImage(MultipartFile file);
    String generateHash();
    byte[] getImage(String imageHash);
}

