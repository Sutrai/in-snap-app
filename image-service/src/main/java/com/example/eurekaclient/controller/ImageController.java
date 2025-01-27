package com.example.eurekaclient.controller;

import com.example.eurekaclient.Util.ImageValidationUtils;
import com.example.eurekaclient.domain.response.SuccessResponse;
import com.example.eurekaclient.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("1/image/")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{imageHash}")
    public ResponseEntity<SuccessResponse<?>> imageInfo(@PathVariable String imageHash){
        return ResponseEntity.ok(SuccessResponse.builder()
                        .status(200)
                        .success(true)
                        .data(imageService.imageInfo(imageHash))
                        .build());
    }

    @GetMapping("/{imageHash}.{extension}")
    public ResponseEntity<?> image(@PathVariable String imageHash){
        byte[] image =  imageService.getImage(imageHash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ImageValidationUtils.detectContentType(image))
                .body(image);
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse<?>> upload(
            @RequestParam("image") Object image,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {

        return ResponseEntity.ok(SuccessResponse.builder()
                        .status(200)
                        .success(true)
                        .data(imageService.uploadImage(image, title, description))
                .build());
    }

    @DeleteMapping("/{imageHash}")
    public ResponseEntity<SuccessResponse<?>> deleteImage(@PathVariable String imageHash){
        return ResponseEntity.ok(SuccessResponse.builder()
                .status(200)
                .success(true)
                .data(imageService.deleteImage(imageHash))
                .build());
    }
}
