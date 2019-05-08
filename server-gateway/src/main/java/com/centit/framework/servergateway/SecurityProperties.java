package com.centit.framework.servergateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SecurityProperties.PREFIX)
@Data
public class SecurityProperties {
    public static final String PREFIX = "security";

    private String loginUrl;
}
