package com.sanifu.order_processing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String fromAddress;
    private String[] allowedDomains;
}
