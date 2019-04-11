package com.centit.framework.session;

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

    @Value("${session.strategy.cookie.first:false}")
    private boolean cookieFist;

    @Value("${session.cookie.path:/}")
    private String cookiePath;

    @Autowired
    private SessionProperties sessionProperties;

    @Bean
    public SmartHttpSessionResolver httpSessionIdResolver(){
        SmartHttpSessionResolver sessionStrategy =  new SmartHttpSessionResolver(cookieFist, cookiePath);
        return sessionStrategy;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        JedisConnectionFactory connection = new JedisConnectionFactory(
            new RedisStandaloneConfiguration(sessionProperties.getRedis().getHost(),
                sessionProperties.getRedis().getPort()));
        return connection;

        //return new LettuceConnectionFactory(sessionProperties.getRedis().getHost(),sessionProperties.getRedis().getPort());
    }
}
