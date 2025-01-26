package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CommonException extends RuntimeException {
    private final Code code;
    private final String error;
    private final String techMessage;
    private final HttpStatus httpStatus;
}
