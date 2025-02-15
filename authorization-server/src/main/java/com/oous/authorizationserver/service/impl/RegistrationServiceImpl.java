package com.oous.authorizationserver.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oous.authorizationserver.component.OTPStore;
import com.oous.authorizationserver.component.RegistrationStore;
import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import com.oous.authorizationserver.domain.constant.Code;
import com.oous.authorizationserver.domain.constant.Role;
import com.oous.authorizationserver.domain.entity.UserEntity;
import com.oous.authorizationserver.domain.response.exception.information.EmailSendingException;
import com.oous.authorizationserver.domain.response.exception.information.InformationException;
import com.oous.authorizationserver.domain.response.exception.information.RegistrationException;
import com.oous.authorizationserver.service.RegistrationService;
import com.oous.authorizationserver.service.UserService;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationStore registrationStore;
    private final UserService userService;
    private final Session mailSession;
    private final OTPStore optStore;
    private final OTPStore otpStore;

    @Override
    public void register(RegistrationReq req, HttpServletResponse response) {
        if (userService.existByEmail(req.getEmail())){
            throw InformationException.builder("error.account.already.exist").build();
        }

        OTPStore.GenerationResult generationResult = optStore.generate(response);

        try {
            registrationStore.save(req, generationResult.sessionId());
        } catch (JsonProcessingException e) {
            throw InformationException.builder("error.json.processing").build();
        }

        sendEmail(req.getEmail(),
                generationResult.opt(),
                generationResult.opt());
    }

    @Override
    public void checkOtp(String otp, HttpServletRequest request) {
        if (!otpStore.validate(otp, request)){
            throw new RegistrationException("otp.incorrect");
        }

        String sessionId = otpStore.getSessionId(request);
        RegistrationReq req;

        try {
            req = registrationStore.take(sessionId);
        } catch (JsonProcessingException e) {
            throw InformationException.builder(Code.NOT_READABLE,
                    "happened.unexpected.error", HttpStatus.BAD_REQUEST).build();
        }
        userService.saveAndActivate(req);
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(to));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (Exception e) {
            throw new EmailSendingException("email.sending.exception");
        }
    }
}
