package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends CommonException {
    public InvalidRequestException() {
        super(Code.UNSUPPORTED_DATA_TYPE,
                "Unsupported data type",
                "Only MultipartFile or URL strings are allowed as input",
                HttpStatus.BAD_REQUEST);
    }
}
