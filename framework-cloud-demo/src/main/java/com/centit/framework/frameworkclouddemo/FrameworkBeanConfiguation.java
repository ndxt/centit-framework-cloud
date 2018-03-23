package com.centit.framework.frameworkclouddemo;

import com.centit.framework.security.model.CentitPasswordEncoderImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;


@Configuration
public class FrameworkBeanConfiguation {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean("passwordEncoder")
    public CentitPasswordEncoderImpl passwordEncoder() {
        return new CentitPasswordEncoderImpl();
    }

    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

}

