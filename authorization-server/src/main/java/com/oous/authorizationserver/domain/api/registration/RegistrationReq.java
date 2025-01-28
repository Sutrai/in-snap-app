package com.oous.authorizationserver.domain.api.registration;

import com.oous.authorizationserver.domain.constant.RegExp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationReq {

    @NotBlank(message = "nickname must be filled")
    @Pattern(regexp = RegExp.NAME, message = "incorrect name")
    private String nickname;

    @NotBlank(message = "email must be filled")
    @Pattern(regexp = RegExp.EMAIL, message = "incorrect email")
    private String email;

    @NotBlank(message = "password must be filled")
    @Pattern(regexp = RegExp.PASSWORD, message = "incorrect password")
    private String password;

}
