package com.centit.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120)
@EnableConfigurationProperties(SessionProperties.class)
public class FrameworkHttpSessionConfiguration {

    @Autowired
    private SessionProperties sessionProperties;

    @Value("${session.redis.host}")
    private String host;

    @Value("${session.redis.port}")
    private Integer port;

    @Bean
    public SmartHttpSessionResolver httpSessionIdResolver(){
        SmartHttpSessionResolver sessionStrategy =
            new SmartHttpSessionResolver(sessionProperties.getCookie().isCookieFirst(),
                sessionProperties.getCookie().getPath());
        return sessionStrategy;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connection = new JedisConnectionFactory(
            new RedisStandaloneConfiguration(host, port));
        return connection;
    }
}
