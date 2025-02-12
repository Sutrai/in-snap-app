package com.oous.authorizationserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class AppProperties {

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties(prefix = "e-mail")
    public static class EmailProperties {
        private String host;
        private int port;
        private String username;
        private String password;
        private String protocol;
        private Properties properties = new Properties();

    }
}
