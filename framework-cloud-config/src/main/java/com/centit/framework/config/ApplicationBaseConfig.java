package com.centit.framework.config;

import com.centit.framework.utils.RestRequestContextInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Configuration
public class ApplicationBaseConfig {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new RestRequestContextInterceptor()));
        } else {
            interceptors.add(new RestRequestContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }
}
