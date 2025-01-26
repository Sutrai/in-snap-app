package com.example.eurekaclient.service.impl;

import com.example.eurekaclient.Util.ConverterToMultipartFile;
import com.example.eurekaclient.Util.ImageValidationUtils;
import com.example.eurekaclient.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final RestTemplate restTemplate;

    @Override
    @SneakyThrows
    public String uploadImage(MultipartFile file){
        ImageValidationUtils.validateImage(file.getBytes());

        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(generateHash());
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    @Override
    public MultipartFile downloadFileFromUrl(String fileUrl) throws Exception {
        ResponseEntity<byte[]> response = restTemplate.exchange(fileUrl, HttpMethod.GET, null, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
            return ConverterToMultipartFile.convert(response.getBody(), generateHash());
        }

        throw new Exception(); //TODO
    }

    @Override
    public MultipartFile validationImage(MultipartFile file) {
        return null;
    }

    @Override
    public String generateHash() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    @Override
    public byte[] getImage(String imageHash) {
        Path filePath = Path.of(uploadDir, imageHash);

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла: " + filePath, e);
        }
    }
}
