package com.oous.authorizationserver.domain.api.login;

import com.oous.authorizationserver.domain.constant.RegExp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginReq {

    @NotBlank(message = "name must be filled")
    @Pattern(regexp = RegExp.NAME, message = "incorrect nickname")
    private String nickname;
    @NotBlank(message = "password must be filled")
    @Pattern(regexp = RegExp.PASSWORD, message = "incorrect email")
    private String password;
}
