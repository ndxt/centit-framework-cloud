package com.centit.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(OAuth2ClientProperties.PREFIX)
public class OAuth2ClientProperties {
    public static final String PREFIX = "spring.security.oauth2.client";

    private String clientId;
    private String clientSecret;
    private String grantType;
    private String authorizationUri;
    private String accessTokenUri;
    private String userInfoUri;
    private String redirectUri;
    private String logOutUri;
}
