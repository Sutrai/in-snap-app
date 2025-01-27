package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class UserDoesNotOwnMediaException extends CommonException {
    public UserDoesNotOwnMediaException() {
        super(Code.USER_NOT_OWNER, "user does not own the media to delete it", null, HttpStatus.FORBIDDEN);
    }
}
