package com.centit.framework.config.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableConfigurationProperties(SessionProperties.class)
public class RedisSessionPersistence {

    @Autowired
    private SessionProperties sessionProperties;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory(sessionProperties.getRedis().getHost(),sessionProperties.getRedis().getPort());
    }
}
