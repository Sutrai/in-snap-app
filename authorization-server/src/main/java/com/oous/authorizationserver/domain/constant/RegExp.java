package com.oous.authorizationserver.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegExp {
    public static final String EMAIL = "^[a-zA-Z0-9_-]{6,25}$";
    public static final String PASSWORD = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?`~]{3,60}$";
    public static final String NAME = "^[a-zA-Z0-9_-]{6,15}$";

}
