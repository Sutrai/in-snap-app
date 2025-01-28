package com.oous.imageservice.domain.response.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class FileDownloadException extends CommonException {
    public FileDownloadException(HttpStatusCode status) {
        super("Failed to download file from URL", "Response status: " + status, HttpStatus.BAD_REQUEST);
    }
}
