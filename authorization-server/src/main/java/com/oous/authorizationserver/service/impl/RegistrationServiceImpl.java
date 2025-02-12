package com.oous.authorizationserver.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oous.authorizationserver.component.OTPStore;
import com.oous.authorizationserver.component.RegistrationStore;
import com.oous.authorizationserver.domain.api.registration.RegistrationReq;
import com.oous.authorizationserver.domain.response.exception.information.InformationException;
import com.oous.authorizationserver.service.RegistrationService;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationStore registrationStore;
    private final Session mailSession;
    private final UserDetailsManager userManager;
    private final OTPStore optStore;

    @Override
    public void register(RegistrationReq req, HttpServletResponse response) {
        if (userManager.userExists(req.getNickname())){
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

    public void sendEmail(String to, String subject, String text) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(to));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
