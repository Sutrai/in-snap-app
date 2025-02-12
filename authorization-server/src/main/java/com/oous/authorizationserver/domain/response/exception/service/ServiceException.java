package com.oous.authorizationserver.domain.response.exception.service;

import com.oous.authorizationserver.domain.constant.Code;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ServiceException extends RuntimeException {
    private final Code code;
    private final String error;
    private final HttpStatus httpStatus;

    public ServiceException(Code code, String error, HttpStatus httpStatus) {
        super(error);
        this.code = code;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public static ServiceExceptionBuilder builder(Code code, String error) {
        return new ServiceExceptionBuilder()
                .code(code)
                .error(error);
    }

    public static ServiceExceptionBuilder builder(String error) {
        return new ServiceExceptionBuilder()
                .error(error);
    }

    public static ServiceExceptionBuilder builder(Code code, String error, HttpStatus httpStatus) {
        return new ServiceExceptionBuilder()
                .code(code)
                .error(error)
                .httpStatus(httpStatus);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class ServiceExceptionBuilder {
        private Code code;
        private String error;
        private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        ServiceExceptionBuilder() {
        }

        public ServiceException build() {
            return new ServiceException(code, error, httpStatus);
        }
    }
}