package com.example.eurekaclient.controller;

import com.example.eurekaclient.Util.ImageValidationUtils;
import com.example.eurekaclient.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/image/")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{imageHash}.{extension}")
    public ResponseEntity<?> image(@PathVariable String imageHash, @PathVariable String extension){

        byte[] image =  imageService.getImage(imageHash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ImageValidationUtils.detectContentType(image))
                .body(image);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "image", required = false) String imageUrl) throws Exception {

        if (image != null) {
            return ResponseEntity.ok(imageService.uploadImage(image));

        } else
            return ResponseEntity.ok(imageService.uploadImage(imageService.downloadFileFromUrl(imageUrl)));

    }
}
