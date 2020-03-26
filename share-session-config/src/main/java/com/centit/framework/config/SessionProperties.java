package com.centit.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(SessionProperties.PREFIX)
public class SessionProperties {
    public static final String PREFIX = "session";

    private Redis redis;

    private Cookie cookie;

    public Cookie getCookie() {
        if(cookie==null){
            cookie = new Cookie();
        }
        return cookie;
    }

    @Data
    public static class Redis{
        public Redis(){
            database = 0;
        }

        private String host;
        private int port;
        private int database;
        private String password;
    }

    @Data
    public static class Cookie{
        private String path;
        private boolean cookieFirst;

        public Cookie(){
            path = "/";
            cookieFirst = false;
        }
    }

}
