package com.centit.product.clouddemo.config.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
//EnableSpringHttpSession
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120)
public class FrameworkHttpSessionConfiguration {

    @Value("${session.strategy.cookie.first:true}")
    private boolean cookieFist;

    @Bean
    public SessionProperties sessionProperties(){
        return new SessionProperties();
    }
}
