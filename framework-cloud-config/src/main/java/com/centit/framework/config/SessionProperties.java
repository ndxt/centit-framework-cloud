package com.centit.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SessionProperties.PREFIX)
public class SessionProperties {
    public static final String PREFIX = "session";

    private Redis redis;

    public Cookie getCookie() {
        if(cookie==null){
            cookie = new Cookie();
        }
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

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
        private String path;
        private boolean cookieFirst;

        public Cookie(){
            path = "/";
            cookieFirst = false;
        }
        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isCookieFirst() {
            return cookieFirst;
        }

        public void setCookieFirst(boolean cookieFirst) {
            this.cookieFirst = cookieFirst;
        }
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }
}
