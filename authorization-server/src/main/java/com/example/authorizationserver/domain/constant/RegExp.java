package com.example.authorizationserver.domain.constant;

public class RegExp {
    public final static String email = "^[a-zA-Z0-9_-]{3,20}$";
    public final static String password = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?`~]{3,60}$";
    public final static String name = "^[a-zA-Z0-9_-]{6,15}$";

}
