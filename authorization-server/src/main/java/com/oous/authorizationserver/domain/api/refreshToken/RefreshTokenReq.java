package com.oous.authorizationserver.domain.api.refreshToken;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenReq {

    @NotBlank(message = "token must be filled")
    private String refreshToken;

}
