package com.oous.authorizationserver.domain.response.exception.information;

import com.oous.authorizationserver.domain.constant.Code;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InformationException extends RuntimeException {
    private final Code code;
    private final String error;
    private final HttpStatus httpStatus;

    public InformationException(Code code, String error, HttpStatus httpStatus) {
        super(error);
        this.code = code;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public static InformationExceptionBuilder builder(Code code, String error) {
        return new InformationExceptionBuilder()
                .code(code)
                .error(error);
    }

    public static InformationExceptionBuilder builder(String error) {
        return new InformationExceptionBuilder()
                .error(error);
    }

    public static InformationExceptionBuilder builder(Code code, String error, HttpStatus httpStatus) {
        return new InformationExceptionBuilder()
                .code(code)
                .error(error)
                .httpStatus(httpStatus);
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class InformationExceptionBuilder {
        private Code code;
        private String error;
        private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        InformationExceptionBuilder() {
        }

        public InformationException build() {
            return new InformationException(code, error, httpStatus);
        }
    }
}