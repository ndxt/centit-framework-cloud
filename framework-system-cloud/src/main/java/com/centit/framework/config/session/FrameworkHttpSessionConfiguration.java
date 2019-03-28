package com.centit.framework.config.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120)
@EnableConfigurationProperties(SessionProperties.class)
public class FrameworkHttpSessionConfiguration {

    @Value("${session.strategy.cookie.first:true}")
    private boolean cookieFist;

    @Autowired
    private SessionProperties sessionProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connection = new JedisConnectionFactory();
        connection.setPort(sessionProperties.getRedis().getPort());
        connection.setHostName(sessionProperties.getRedis().getHost());
        return connection;

        //return new LettuceConnectionFactory(sessionProperties.getRedis().getHost(),sessionProperties.getRedis().getPort());
    }
}
