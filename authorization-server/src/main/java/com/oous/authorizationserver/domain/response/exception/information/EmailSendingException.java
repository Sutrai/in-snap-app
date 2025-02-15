package com.oous.authorizationserver.domain.response.exception.information;

import com.oous.authorizationserver.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class EmailSendingException extends InformationException {
    public EmailSendingException(String error) {
        super(Code.EMAIL_SENDING_EXCEPTION, error, HttpStatus.BAD_REQUEST);
    }
}
