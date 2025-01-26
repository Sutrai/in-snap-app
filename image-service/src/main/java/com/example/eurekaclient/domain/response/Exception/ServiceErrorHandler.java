package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import com.example.eurekaclient.domain.response.Error.Data;
import com.example.eurekaclient.domain.response.Error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Arrays;

@ControllerAdvice
public class ServiceErrorHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex){

        return new ResponseEntity<>(ErrorResponse.builder()
                .data(Data.builder()
                        .code(ex.getCode())
                        .error(ex.getError())
                        .techMessage(ex.getTechMessage())
                        .build())
                .success("false").status(ex.getHttpStatus().value()).build(),
                ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrorException(Exception ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                        .data(Data.builder()
                                .code(Code.NOT_READABLE)
                                .error("Internal Server Error")
                                .techMessage(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()))
                                .build())
                        .success("false").status(500).build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .data(Data.builder()
                        .code(Code.INVALID_CREDENTIALS)
                        .error("Invalid username or password")
                        .techMessage(ex.getMessage())
                        .build())
                .success("false").status(500).build(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .data(Data.builder()
                        .code(Code.PAYLOAD_TOO_LARGE)
                        .error("Maximum upload size exceeded")
                        .techMessage("The uploaded file exceeds the allowed size limit of 10MB")
                        .build())
                .success("false").status(400).build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
