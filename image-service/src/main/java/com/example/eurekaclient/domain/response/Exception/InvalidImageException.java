package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
public class InvalidImageException extends CommonException {
    public InvalidImageException(Code code, String techMessage) {
        super(code, "Invalid image provided", techMessage, HttpStatus.BAD_REQUEST);
    }
}
