package com.centit.framework.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 120)
@EnableConfigurationProperties(SessionProperties.class)
public class FrameworkHttpSessionConfiguration {

    @Autowired
    private SessionProperties sessionProperties;

    @Bean
    public SmartHttpSessionResolver httpSessionIdResolver(){
        SmartHttpSessionResolver sessionStrategy =
            new SmartHttpSessionResolver(sessionProperties.getCookie().isCookieFirst(),
                sessionProperties.getCookie().getPath());
        return sessionStrategy;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
            new RedisStandaloneConfiguration(sessionProperties.getRedis().getHost(),
                sessionProperties.getRedis().getPort());

        String password = sessionProperties.getRedis().getPassword();
        if(StringUtils.isNotBlank(password)){
            configuration.setPassword(RedisPassword.of(password));
        }

        return new JedisConnectionFactory(configuration);
    }
}
