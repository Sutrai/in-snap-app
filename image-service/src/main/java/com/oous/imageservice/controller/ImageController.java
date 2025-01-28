package com.oous.imageservice.controller;

import com.oous.imageservice.util.ImageValidationUtils;
import com.oous.imageservice.domain.response.Response;
import com.oous.imageservice.domain.response.SuccessResponse;
import com.oous.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("1/image/")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{imageHash}")
    public ResponseEntity<Response> imageInfo(@PathVariable String imageHash){
        return ResponseEntity.ok(SuccessResponse.builder()
                        .status(200)
                        .success(true)
                        .data(imageService.imageInfo(imageHash))
                        .build());
    }

    @GetMapping("/{imageHash}.{extension}")
    public ResponseEntity<byte[]> image(@PathVariable String imageHash, @PathVariable String extension){
        byte[] image =  imageService.getImage(imageHash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ImageValidationUtils.detectContentType(image))
                .body(image);
    }

    @PostMapping("/upload")
    public ResponseEntity<Response> upload(
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
    public ResponseEntity<Response> deleteImage(@PathVariable String imageHash){
        return ResponseEntity.ok(SuccessResponse.builder()
                .status(200)
                .success(true)
                .data(imageService.deleteImage(imageHash))
                .build());
    }
}
