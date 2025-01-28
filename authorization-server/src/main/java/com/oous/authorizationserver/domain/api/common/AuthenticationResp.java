package com.oous.authorizationserver.domain.api.common;

import com.oous.authorizationserver.domain.response.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResp implements Response {

    private String accessToken;
    private String scope;
    private String refreshToken;
    private String accountId;
    private String accountNickname;
}
