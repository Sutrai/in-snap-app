package com.oous.authorizationserver.domain.api.registration;

import com.oous.authorizationserver.domain.response.Response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResp implements Response {

    private String accessToken;
    private String refreshToken;
}
