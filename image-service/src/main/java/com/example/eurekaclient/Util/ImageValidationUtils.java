package com.example.eurekaclient.Util;

import com.example.eurekaclient.domain.constant.Code;
import com.example.eurekaclient.domain.response.Exception.InvalidImageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

public class ImageValidationUtils {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 5000;
    private static final int MAX_HEIGHT = 5000;

    public static void validateImage(byte[] imageBytes) throws InvalidImageException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new InvalidImageException(Code.PAYLOAD_TOO_LARGE, "Image is empty or null");
        }

        if (imageBytes.length > MAX_FILE_SIZE) {
            throw new InvalidImageException(Code.PAYLOAD_TOO_LARGE, "Image file is too large. Maximum allowed size is " + MAX_FILE_SIZE / (1024 * 1024) + "MB");
        }

        String contentType = detectContentType(imageBytes);
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidImageException(Code.INVALID_IMAGE, "Invalid image type. Allowed types are: " + String.join(", ", ALLOWED_CONTENT_TYPES));
        }

        validateResolution(imageBytes);
    }

    private static void validateResolution(byte[] imageBytes) throws InvalidImageException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new InvalidImageException(Code.INVALID_IMAGE, "The file is not a valid image.");
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width < MIN_WIDTH || height < MIN_HEIGHT) {
                throw new InvalidImageException(Code.REQUEST_VALIDATION_ERROR, "Image resolution is too small. Minimum allowed resolution is " + MIN_WIDTH + "x" + MIN_HEIGHT);
            }

            if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                throw new InvalidImageException(Code.REQUEST_VALIDATION_ERROR, "Image resolution is too large. Maximum allowed resolution is " + MAX_WIDTH + "x" + MAX_HEIGHT);
            }
        } catch (IOException e) {
            throw new InvalidImageException(Code.NOT_READABLE, "Error reading image resolution.");
        }
    }

    public static String detectContentType(byte[] imageBytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
            String contentType = URLConnection.guessContentTypeFromStream(byteArrayInputStream);

            if (contentType == null || contentType.isEmpty()) {
                throw new InvalidImageException(Code.INVALID_IMAGE, "Unknown file extension");
            }
            return contentType;
        } catch (IOException e) {
            throw new InvalidImageException(Code.NOT_READABLE, "Error detecting content type.");
        }
    }
}