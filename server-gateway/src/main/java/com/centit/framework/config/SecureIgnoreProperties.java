package com.centit.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(SecureIgnoreProperties.PREFIX)
public class SecureIgnoreProperties {
    public static final String PREFIX = "secure.ignore";

    private List<String> urls;
}
