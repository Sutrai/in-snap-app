package com.oous.imageservice.service.impl;

import com.oous.imageservice.util.ConverterToMultipartFile;
import com.oous.imageservice.util.ImageValidationUtils;
import com.oous.imageservice.dao.ImageDao;
import com.oous.imageservice.domain.response.exception.FileDownloadException;
import com.oous.imageservice.domain.response.exception.InvalidImageException;
import com.oous.imageservice.domain.response.exception.UserDoesNotOwnMediaException;
import com.oous.imageservice.dto.Image;
import com.oous.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.Imaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${external.api.url}")
    private String url;
    private final RestTemplate restTemplate;
    private final ImageDao imageDao;

    @Override
    @SneakyThrows
    public Image uploadImage(Object image, String title, String description) {
        MultipartFile file;

        if (image instanceof MultipartFile fileMultipart) file = fileMultipart;
        else file = downloadFileFromUrl((String) image);

        ImageValidationUtils.validateImage(file.getBytes());
        String imageHash = generateHash();

        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(imageHash);
        file.transferTo(filePath.toFile());

        ImageInfo imageInfo = Imaging.getImageInfo(filePath.toFile());

        Image images = Image.builder()
                .imageHash(filePath.getFileName().toString())
                .accountId(getCurrentAccountId())
                .title(title)
                .description(description)
                .type(file.getContentType())
                .width(imageInfo.getWidth())
                .height(imageInfo.getHeight())
                .size(file.getSize())
                .link(String.format("%s%s.%s", url, imageHash, imageInfo.getFormatDetails().toLowerCase()))
                .build();

        imageDao.saveImage(images);
        return images;
    }

    @Override
    public MultipartFile downloadFileFromUrl(String fileUrl){
        ResponseEntity<byte[]> response = restTemplate.exchange(fileUrl, HttpMethod.GET, null, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
            return ConverterToMultipartFile.convert(response.getBody(), generateHash());
        }

        throw new FileDownloadException(response.getStatusCode());
    }

    @Override
    public String generateHash() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    @Override
    public byte[] getImage(String imageHash) {
        try {
            return Files.readAllBytes(Path.of(uploadDir, imageHash));
        } catch (IOException e) {
            throw new InvalidImageException("Error reading file: " + e.getMessage());
        }
    }

    @Override
    public Image imageInfo(String imageHash) {
        return imageDao.getImage(imageHash);
    }

    @Override
    public boolean deleteImage(String imageHash) {
        Image image = imageDao.getImage(imageHash);

        if (image.getAccountId() == getCurrentAccountId())
            return imageDao.deleteImage(imageHash, getCurrentAccountId());

        else throw new UserDoesNotOwnMediaException();
    }

    @Override
    public long getCurrentAccountId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
}
