package com.oous.authorizationserver.domain.api.refreshToken;

import com.oous.authorizationserver.domain.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResp implements Response {

    private String accessToken;
    private String bearer;
    private String scope;
    private String refreshToken;
    private String accountId;
    private String accountNickname;
}
