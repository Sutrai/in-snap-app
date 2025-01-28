package com.oous.imageservice.util;

import com.oous.imageservice.domain.constants.Constants;
import com.oous.imageservice.domain.response.exception.InvalidImageException;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

@UtilityClass
public class ImageValidationUtils {

    public static void validateImage(byte[] imageBytes) throws InvalidImageException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new InvalidImageException("Image is empty or null");
        }

        String contentType = detectContentType(imageBytes);
        if (!Constants.ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidImageException("We don't support that file type!");
        }

        validateResolution(imageBytes);
    }

    private static void validateResolution(byte[] imageBytes) throws InvalidImageException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new InvalidImageException("The file is not a valid image.");
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width < Constants.MIN_WIDTH || height < Constants.MIN_WIDTH) {
                throw new InvalidImageException("Image resolution is too small. Minimum allowed resolution is " + Constants.MIN_WIDTH + "x" + Constants.MIN_HEIGHT);
            }

            if (width > Constants.MAX_WIDTH || height > Constants.MAX_HEIGHT) {
                throw new InvalidImageException("Image resolution is too large. Maximum allowed resolution is " + Constants.MAX_WIDTH + "x" + Constants.MAX_HEIGHT);
            }
        } catch (IOException e) {
            throw new InvalidImageException("Error reading image resolution.");
        }
    }

    public static String detectContentType(byte[] imageBytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
            return URLConnection.guessContentTypeFromStream(byteArrayInputStream);

        } catch (IOException e) {
            throw new InvalidImageException("Error detecting content type.");
        }
    }
}