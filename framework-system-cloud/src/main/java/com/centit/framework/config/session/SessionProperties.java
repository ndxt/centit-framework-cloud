package com.centit.framework.config.session;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SessionProperties.PREFIX)
public class SessionProperties {
    public static final String PREFIX = "session";

    private Redis redis;

    private Cookie cookie;

    public static class Redis{
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }


    public static class Cookie{
        String path;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }
}
