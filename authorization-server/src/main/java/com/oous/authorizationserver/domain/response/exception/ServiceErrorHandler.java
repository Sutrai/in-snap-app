package com.oous.authorizationserver.domain.response.exception;

import com.oous.authorizationserver.domain.response.error.Data;
import com.oous.authorizationserver.domain.response.error.ErrorResponse;
import com.oous.authorizationserver.domain.response.exception.information.InformationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ServiceErrorHandler {

    @ExceptionHandler(InformationException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(InformationException ex, HttpServletRequest request){

        logRequestException(request, ex);

        return new ResponseEntity<>(ErrorResponse.builder()
                .data(Data.builder()
                        .error(ex.getError())
                        .method(request.getMethod())
                        .request(request.getRequestURI())
                        .build())
                .informative(true)
                .success(false).status(ex.getHttpStatus().value()).build(),
                ex.getHttpStatus());
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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .data(Data.builder()
                        .error("Invalid username or password")
                        .techMessage(ex.getMessage())
                        .method(request.getMethod())
                        .request(request.getRequestURI())
                        .build())
                .success(false).status(HttpStatus.UNAUTHORIZED.value()).build(),
                HttpStatus.UNAUTHORIZED
        );
    }

    private void logRequestException(HttpServletRequest request, Exception exception) {
        log.debug("Unexpected exception processing request: " + request.getRequestURI());
        log.error("Exception: ", exception);
    }
}
