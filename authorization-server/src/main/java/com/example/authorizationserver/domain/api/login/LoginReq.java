package com.example.authorizationserver.domain.api.login;

import com.example.authorizationserver.domain.constant.RegExp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginReq {

    @NotBlank(message = "name must be filled")
    @Pattern(regexp = RegExp.name, message = "incorrect nickname")
    private String nickname;
    @NotBlank(message = "password must be filled")
    @Pattern(regexp = RegExp.password, message = "incorrect email")
    private String password;
}
