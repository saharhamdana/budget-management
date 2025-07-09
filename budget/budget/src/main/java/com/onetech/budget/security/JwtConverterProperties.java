package com.onetech.budget.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt.auth.convertor")
public class JwtConverterProperties {
    private String resourceId;
    private String principalAttribute;
}
