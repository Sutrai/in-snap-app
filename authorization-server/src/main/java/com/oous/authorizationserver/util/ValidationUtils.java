package com.oous.authorizationserver.util;

import com.oous.authorizationserver.domain.constant.Code;
import com.oous.authorizationserver.domain.response.exception.information.InvalidCredentialsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationUtils {

    private final Validator validator;

    public <T> void validationRequest(T req) {
        Set<ConstraintViolation<T>> result = validator.validate(req);

        if (!result.isEmpty()) {
            String resultValidation = result.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((s1, s2) -> s1 + ". " + s2).orElse("");

            log.info("Validation failed: {}", resultValidation);

            throw new InvalidCredentialsException("validation.error.message");

        } else log.info("Validation successful for request: {}", req);

    }
}
