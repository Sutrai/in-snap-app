package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class MediaNotFoundException extends CommonException {
    public MediaNotFoundException() {
        super(Code.NOT_FOUND, "media not found", null, HttpStatus.NOT_FOUND);
    }
}
