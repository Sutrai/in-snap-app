package com.oous.authorizationserver.domain.response.exception.information;

import com.oous.authorizationserver.domain.constant.Code;
import org.springframework.http.HttpStatus;

public class RegistrationException extends InformationException {
  public RegistrationException(String error) {
    super(Code.VALIDATE_ERROR, error, HttpStatus.BAD_REQUEST);
  }
}
