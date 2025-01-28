package com.oous.imageservice.domain.response.exception;

import com.oous.imageservice.domain.response.error.Data;
import com.oous.imageservice.domain.response.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Arrays;

@ControllerAdvice
public class ServiceErrorHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .data(Data.builder()
                                .error(ex.getError())
                                .techMessage(ex.getTechMessage())
                                .method(request.getMethod())
                                .request(request.getRequestURI())
                                .build())
                        .success(false).status(ex.getHttpStatus().value()).build(),
                ex.getHttpStatus()
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrorException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                        .data(Data.builder()
                                .error("Internal Server Error")
                                .techMessage(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()))
                                .method(request.getMethod())
                                .request(request.getRequestURI())
                                .build())
                        .success(false).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                        .data(Data.builder()
                                .error("Maximum upload size exceeded")
                                .techMessage("The uploaded file exceeds the allowed size limit of 10MB")
                                .method(request.getMethod())
                                .request(request.getRequestURI())
                                .build())
                        .success(false).status(ex.getStatusCode().value()).build(),
                ex.getStatusCode()
        );
    }
}
