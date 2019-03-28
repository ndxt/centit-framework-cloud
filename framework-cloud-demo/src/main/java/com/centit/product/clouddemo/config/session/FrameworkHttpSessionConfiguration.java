package com.centit.product.clouddemo.config.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableSpringHttpSession
@EnableRedisHttpSession
@EnableConfigurationProperties(SessionProperties.class)
public class FrameworkHttpSessionConfiguration {

    @Value("${session.strategy.cookie.first:true}")
    private boolean cookieFist;

    @Autowired
    private SessionProperties sessionProperties;

    @Bean
    public SmartHttpSessionStrategy smartHttpSessionStrategy(){
        SmartHttpSessionStrategy sessionStrategy =  new SmartHttpSessionStrategy();
        sessionStrategy.setCookieFirst(cookieFist);
        return sessionStrategy;
    }

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(sessionProperties.getRedis().getHost(),sessionProperties.getRedis().getPort());
    }
}
