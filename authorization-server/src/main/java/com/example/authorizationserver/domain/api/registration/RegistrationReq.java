package com.example.authorizationserver.domain.api.registration;

import com.example.authorizationserver.domain.constant.RegExp;
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
    @Pattern(regexp = RegExp.name, message = "incorrect name")
    private String nickname;

    @NotBlank(message = "email must be filled")
    @Pattern(regexp = RegExp.email, message = "incorrect email")
    private String email;

    @NotBlank(message = "password must be filled")
    @Pattern(regexp = RegExp.password, message = "incorrect password")
    private String password;

}
