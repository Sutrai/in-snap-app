package com.oous.authorizationserver.domain.api.login;

import com.oous.authorizationserver.domain.response.Response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResp implements Response {

    private String accessToken;
    private String refreshToken;
}
